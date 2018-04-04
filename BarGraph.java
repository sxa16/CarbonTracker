package com.sfu.aqua.carbontracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sfu.aqua.carbontracker.models.Day;
import com.sfu.aqua.carbontracker.models.DayList;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.util.ArrayList;
import java.util.Locale;

import static java.util.Arrays.copyOfRange;

public class BarGraph extends AppCompatActivity {

    String[] x_labels;
    float[][] emissions;
    int daysCount;
    int flag;
    float laptopHours = 0.012f;
    private final static String[] month_String = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private final static int[] COLORS_SET = {
            Color.rgb(176, 224, 230), Color.rgb(222, 184, 135), Color.rgb(255, 160, 122), Color.rgb(238, 197, 145), Color.rgb(164, 211, 238), Color.rgb(154, 205, 50)
    };
    BarData data;


    public static Intent makeIntent(Context context) {
        return new Intent(context, BarGraph.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
        extractFlag();
        initializeRadioGroup();
        findViewById(R.id.rdbtn_28).performClick();
        changeFont((TextView) findViewById(R.id.textView15));
        changeFont((TextView) findViewById(R.id.rdbtn_28));
        changeFont((TextView) findViewById(R.id.rdbtn_365));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.footprint_graph, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                super.onBackPressed();
                break;
            case R.id.home:
                Intent intent = new Intent(BarGraph.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",flag);
        Log.i("IN SELECT transport", " " + flag);
        if(flag == 1){
            TextView textview = (TextView) findViewById(R.id.textView15);
            textview.setText("laptop hours");
        }
    }

    public void initializeRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rdgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                daysCount = (checkedId == R.id.rdbtn_28) ? 28 : 365;
                DayList dayLists = getDayListAfterTime(Singleton.dayList, getCalendarDateBefore(daysCount - 1));

                if (dayLists == null) return;

                Calendar[] calendarDays = new Calendar[daysCount];
                emissions = new float[daysCount][4];
                //emissions = new float[12][4];

                for (int i = 0; i < daysCount; i++) {
                    calendarDays[i] = getCalendarDateBefore(daysCount - 1 - i);

                    Day tmpDay = dayLists.getDay(calendarDays[i], false);

                    if (tmpDay == null) {
                        for (int k = 0; k < emissions[0].length; k++)
                            emissions[i][k] = 0f;
                    } else {
                        if(flag == 0){
                            emissions[i][0] = (float) tmpDay.getBusEmissions();
                            emissions[i][1] = (float) tmpDay.getSkytrainEmissions();
                            emissions[i][2] = (float) tmpDay.getBikeWalkEmissions();
                            emissions[i][3] = (float) tmpDay.getCarEmissions();
                        }
                        else{
                            emissions[i][0] = (float) tmpDay.getBusEmissions()/laptopHours;
                            emissions[i][1] = (float) tmpDay.getSkytrainEmissions()/laptopHours;
                            emissions[i][2] = (float) tmpDay.getBikeWalkEmissions()/laptopHours;
                            emissions[i][3] = (float) tmpDay.getCarEmissions()/laptopHours;
                        }
                        //emissions[i][0] = (float) tmpDay.getBusEmissions();
                        //emissions[i][1] = (float) tmpDay.getSkytrainEmissions();
                        //emissions[i][2] = (float) tmpDay.getBikeWalkEmissions();
                        //emissions[i][3] = (float) tmpDay.getCarEmissions();
                        //emissions[i][4] = (float) tmpDay.getElectricityEmissions();
                        //emissions[i][5] = (float) tmpDay.getGasEmissions();
                        //System.out.println("====666====" + calendarDateToString(tmpDay.getDate()) + " --- " + tmpDay.getElectricityEmissions());
                    }

                }

                if (daysCount < 31) {
                    x_labels = new String[daysCount];
                    for (int i = 0; i < x_labels.length; i++)
                        x_labels[i] = calendarDateToString(calendarDays[i]);
                } else {
                    x_labels = new String[12];
                    Calendar tmpCalendar = Calendar.getInstance();
                    int month = tmpCalendar.get(Calendar.MONTH) + 1;
                    int year = tmpCalendar.get(Calendar.YEAR);

                    float[][] tmp_emissions = new float[12][emissions[0].length];

                    for (int i = 12 - 1; i >= 0; i--) {
                        for (int k = 0; k < calendarDays.length; k++) {
                            if (month == (calendarDays[k].get(Calendar.MONTH) + 1) && year == calendarDays[k].get(Calendar.YEAR)) {
                                for (int j = 0; j < emissions[0].length; j++)
                                    tmp_emissions[i][j] += emissions[k][j];
                            }
                        }

                        if (month > 1) {
                            month--;
                        } else if (month == 1) {
                            month = 12;
                            year--;
                        }
                        x_labels[i] = String.valueOf(month_String[month - 1]) + "." + String.valueOf(year);

                    }
                    emissions = tmp_emissions;

                }
                setupBarChart();
            }
        });
    }

    private String getPrevMonthYear(int month, int year) {

        if (month > 1)
            return String.valueOf(month_String[month - 2]) + "." + String.valueOf(year);
        else if (month == 1)
            return String.valueOf(month_String[11]) + "." + String.valueOf(year - 1);
        else
            throw new IllegalArgumentException();
    }

    private static String calendarDateToString(Calendar date) {
        return calendarDateToString(date, "MMM.dd");
    }

    private static String calendarDateToString(Calendar date, String format) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, Locale.US);
        return sdf.format(date.getTime());
    }

    private static Calendar getCalendarDateBefore(int day) {
        return getCalendarDateBefore(Calendar.getInstance(), day);
    }

    private static Calendar getCalendarDateBefore(Calendar calendar, int day) {
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - day);
        return calendar;
    }


    private static Calendar getDateAfter(int day) {
        return getDateAfter(Calendar.getInstance(), day);
    }

    private static Calendar getDateAfter(Calendar calendar, int day) {
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
        return calendar;
    }


    private DayList getDayListAfterTime(DayList dl, Calendar calendar) {
        return getDayListAfterTime(dl, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Nullable
    private DayList getDayListAfterTime(DayList dl, int year, int month, int day) {
        //if (dl == null) return null;
        if (dl.size() == 0) return null;

        int yyyyddmm_after = year * 10000 + month * 100 + day;
        DayList ret = new DayList();

        for (Day dayFull : dl) {
            int yyyyddmm_curr = Integer.parseInt(calendarDateToString(dayFull.getDate(), "yyyyMMdd"));
            if (yyyyddmm_curr > yyyyddmm_after)
                ret.add(dayFull);
        }
        if (ret.size() == 0) return null;
        return ret;
    }


    private void setupBarChart() {
        // if (routes.length > 0) {
        CombinedChart bChart = (CombinedChart) findViewById(R.id.bChart);
        YAxis leftAxis = bChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        bChart.getAxisRight().setEnabled(false);
        bChart.setScaleEnabled(false);

        XAxis xLabels = bChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

        xLabels.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return x_labels[(int) value % x_labels.length];
            }
        });

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < emissions.length; i++)
            yVals1.add(new BarEntry(i, emissions[i]));
        BarDataSet set1;
        if (bChart.getBarData() != null && bChart.getBarData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bChart.getBarData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            bChart.getData().notifyDataChanged();
            bChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setValueTextSize(0);
            set1.setColors(getColors(emissions[0].length));
            set1.setStackLabels(new String[]{"Bus", "Skytrain", "Bike/Walk", "Car"});   //, "Electricity", "Gas"
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
          data=new BarData(dataSets);
            //data.setValueFormatter(new MyValueFormatter());
           // data.setValueTextColor(Color.WHITE);

        }
        ArrayList<ILineDataSet> lines = new ArrayList<>();
        ArrayList<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < data.getEntryCount(); ++i) {
            lineEntries.add(new Entry(i, 4));
        }
        LineDataSet line = new LineDataSet(lineEntries, "average");
        line.setDrawCircles(false);
        line.setColor(Color.RED);
        lines.add(line);

        ArrayList<Entry> lineEntriesTgt = new ArrayList<>();
        lineEntriesTgt.add(new Entry(0, 3));
        for (int i = 1; i < data.getEntryCount(); ++i) {
            lineEntriesTgt.add(new Entry(i, 3));

        }
        LineDataSet lineTgt = new LineDataSet(lineEntriesTgt, "target");
        lineTgt.setDrawCircles(false);
        lineTgt.setColor(Color.GREEN);
        lines.add(lineTgt);
        CombinedData cData=new CombinedData();
        cData.setData(data);
        cData.setData(new LineData(lines));

        bChart.setData(cData);
        bChart.getLegend().setEnabled(false);

        Description desc = new Description();
        desc.setEnabled(false);

        Description description = new Description();
        description.setText("");
        bChart.setDescription(description);
        bChart.invalidate();
    }

    private int[] getColors(int stacksize) {
        return copyOfRange(COLORS_SET, 0, stacksize);
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);}
    private void changeFont(Button btn) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        btn.setTypeface(typeface);}

}
