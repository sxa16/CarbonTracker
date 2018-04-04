package com.sfu.aqua.carbontracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sfu.aqua.carbontracker.models.Day;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.util.ArrayList;
import java.util.List;

public class PieGraph extends AppCompatActivity {
    public static final double laptopHours = .012;
    Calendar calendar = Calendar.getInstance();
    double emission[];
    String divide[];
    int flag;
    int status=0;//0: by mode 1: by route
    int selection=0;//1: 1 day 28:28days 365:365 days
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_graph);
        extractFlag();
        setupDateBtn();
        setupMultiple();
        setupSwitchBtn();

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
                Intent intent = new Intent(PieGraph.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupMultiple() {
        Button button=(Button)findViewById(R.id.btn28);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection=28;
                setupList();
                setupChart();
            }
        });
        Button button1=(Button)findViewById(R.id.btn365);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection=365;
                setupList();
                setupChart();
            }
        });
    }

    private void setupSwitchBtn() {
        Button button=(Button)findViewById(R.id.switchBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupList();
                setupChart();
            }
        });
    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        Log.i("IN SELECT", " " + flag);
    }

    private void setupChart() {
        if (divide.length > 0) {
            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < divide.length; i++)
                pieEntries.add(new PieEntry((float) emission[i], divide[i]));
            PieDataSet dataSet = new PieDataSet(pieEntries, "[Different Emissions]");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(16);
            PieChart chart = (PieChart) findViewById(R.id.chart);
            chart.setData(data);
            chart.animateY(1000);
            Description description = new Description();
            if(flag ==0){
                description.setText("CO₂ Emission Data");
                description.setTextSize(16);
            }
            else{
                description.setText("laptop hours");
                description.setTextSize(16);
            }
            chart.setDescription(description);
            chart.invalidate();
        }
    }

    private void setupList() {
        Day day = Singleton.dayList.getDay(calendar);
        if(status==1) {//by mode

            divide = new String[]{"Bus", "Skytrain", "Bike/Walk", "Car", "Gas", "Electricity"};
            emission = new double[6];
            for(int i=0;i<6;i++){
                emission[i]=0;
            }
            if(selection==1) {//one day
                if (flag == 0) {
                    emission[0] = (float) day.getBusEmissions();
                    emission[1] = (float) day.getSkytrainEmissions();
                    emission[2] = (float) day.getBikeWalkEmissions();
                    emission[3] = (float) day.getCarEmissions();
                    emission[4] = (float) day.getGasEmissions();
                    emission[5] = (float) day.getElectricityEmissions();
                } else {
                    emission[0] = (float) day.getBusEmissions() / laptopHours;
                    emission[1] = (float) day.getSkytrainEmissions() / laptopHours;
                    emission[2] = (float) day.getBikeWalkEmissions() / laptopHours;
                    emission[3] = (float) day.getCarEmissions() / laptopHours;
                    emission[4] = (float) day.getGasEmissions() / laptopHours;
                    emission[5] = (float) day.getElectricityEmissions() / laptopHours;
                }
            }
            else {//multiple days
               Calendar iteratingDate=Calendar.getInstance();
                int count=0;
                while(count<selection){
                    Log.i("ss","ddd");
                        Day days = Singleton.dayList.getDay(iteratingDate);
                        emission[0] += (float) days.getBusEmissions();
                        emission[1] += (float) days.getSkytrainEmissions();
                        emission[2] += (float) days.getBikeWalkEmissions();
                        emission[3] += (float) days.getCarEmissions();
                        emission[4] += (float) days.getGasEmissions();
                        emission[5] += (float) days.getElectricityEmissions();
                        iteratingDate.add(Calendar.DATE, -1);
                         count+=1;
                }
                if(flag!=0){
                    for(int i=0;i<6;i++)
                        emission[i]/=laptopHours;
                }
            status=0;
            }
        }
        else if(status==0){
            int size=Singleton.routeList.size();
            divide=new String[size+3];
            emission=new double[size+3];
            for(int i=0;i<emission.length;i++){
                emission[i]=0;
            }
            if(selection==1) {
                for (int i = 0; i < size; i++) {
                    if (flag == 0) {
                        divide[i] = Singleton.routeList.get(i).getName();
                        if(day.getRouteListOfTheDay().size()>0)
                        emission[i] = (float) day.getRouteOfTheDay(Singleton.routeList.get(i)).getEmission();
                    } else {
                        divide[i] = Singleton.routeList.get(i).getName();
                        if(day.getRouteListOfTheDay().size()>0)
                        emission[i] = (float) day.getRouteOfTheDay(Singleton.routeList.get(i)).getEmission() / laptopHours;
                    }

                }
            }
            else{
                for(int i=0;i<size;i++){
                    divide[i]=Singleton.routeList.get(i).getName();
                }
                Calendar iteratingDate=Calendar.getInstance();
                int count=0;
                while(count<selection){
                    Log.i("ss","ddtt");
                    Day days = Singleton.dayList.getDay(iteratingDate);
                    for(int i=0;i<size;i++){
                       emission[i]+=(float)days.getRouteOfTheDay(Singleton.routeList.get(i)).getEmission();
                    }
                    iteratingDate.add(Calendar.DATE, -1);
                    count+=1;
                }
            }
            divide[size]="Others" ;
            divide[size+1]="Gas";
            divide[size+2]="Electricity";


            Calendar iteratingDate2=Calendar.getInstance();
            int count=0;
            while(count<selection){
                Log.i("ss","ddtt");
                Day days = Singleton.dayList.getDay(iteratingDate2);
                emission[size]+=(float)days.getEmissionFromAutouse();
                emission[size+1]+=(float)days.getGasEmissions();
                emission[size+2]+=(float)days.getElectricityEmissions();

                iteratingDate2.add(Calendar.DATE, -1);
                count+=1;
            }
            status=1;

        }

    }

    private void setupDateBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.pieDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection=1;
                final DatePickerDialog date =
                        new DatePickerDialog(PieGraph.this,
                                listener, calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );
                date.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calendar.set(date.getDatePicker().getYear(),
                                date.getDatePicker().getMonth(),
                                date.getDatePicker().getDayOfMonth());
                        setupList();
                        setupChart();
                    }
                });
                date.show();
            }

            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);

                }
            };
        });
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}






