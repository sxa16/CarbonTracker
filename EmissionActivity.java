package com.sfu.aqua.carbontracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sfu.aqua.carbontracker.models.Car;
import com.sfu.aqua.carbontracker.models.Day;
import com.sfu.aqua.carbontracker.models.Journey;
import com.sfu.aqua.carbontracker.models.Route;
import com.sfu.aqua.carbontracker.models.Singleton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmissionActivity extends AppCompatActivity {
    private List<Journey> myJourneys = new ArrayList<Journey>();
    public static final double laptopHours = .012;
    int flag;
    public static Intent makeIntent(Context context) {
        return new Intent(context, EmissionActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emission);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
        extractFlag();
        changeFont((TextView) findViewById(R.id.textView24));
        setupGraphBtn();
        populateListView();
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
                Intent intent = new Intent(EmissionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void populateJourneyList() {
        myJourneys.clear();
        for (Journey journey : Singleton.journeyList) {
            myJourneys.add(journey);
            //myJourneys.add(new Journey(journey.getDate(), journey.getRoute(), journey.getEmission(), journey.getTransportation()));
        }
    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("unitFlag", 0);
        Log.i("IN SELECT", " " + flag);
    }

    private void populateListView() {
        populateJourneyList();
        ArrayAdapter<Journey> adapter = new JourneyListAdapter();
        ListView list = (ListView) findViewById(R.id.emission);
        list.setAdapter(adapter);
    }

    private class JourneyListAdapter extends ArrayAdapter<Journey> {
        public JourneyListAdapter() {
            super(EmissionActivity.this, R.layout.journey_view, myJourneys);
            //System.out.println("=======" + myCars.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.journey_view, parent, false);
            }
            Journey currentJourney = myJourneys.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_journey);
            imageView.setImageResource(currentJourney.getIcon());


            // Condition:
            TextView condtionText = (TextView) itemView.findViewById(R.id.txt_transportation);
            condtionText.setText(currentJourney.getName());

            // Make:
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
            TextView makeText = (TextView) itemView.findViewById(R.id.txt_date);
            makeText.setText(dateFormat.format(currentJourney.getDate().getTime()));

            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.txt_emission);
            if(flag == 0) {
                yearText.setText(String.format("%.2f", currentJourney.getEmission()) + "g. ");
            }
            else{
                yearText.setText(String.valueOf("" +String. format(".2f", currentJourney.getEmission()/ laptopHours))+"Laptop-hrs");
            }

            TextView modelText = (TextView) itemView.findViewById(R.id.txt_distance);
            modelText.setText(currentJourney.getDistance() + "km   ");


            return itemView;
        }
    }

    /*private void populateListView() {
        TableLayout tl = (TableLayout) findViewById(R.id.tbMain);


        for (Journey journey : Singleton.journeyList) {
            Car car = journey.getCar();
            Route route = journey.getRoute();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            TextView tv_date = new TextView(this);
            tv_date.setText(dateFormat.format(journey.getDate()));
            tv_date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView tv_name = new TextView(this);
            tv_name.setText(journey.getRoute().getName());
            tv_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView tv_distance = new TextView(this);
            double distance = route.getCity()+ route.getHighway();
            tv_distance.setText(String.valueOf(distance));
            tv_distance.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TextView tv_vehicle = new TextView(this);
            if(journey.getTransportation()==0) {
                String carIndex = car.getNickname();
                tv_vehicle.setText(carIndex);
                tv_vehicle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            else if(journey.getTransportation()==1) {
                String carIndex = "Bus";
                tv_vehicle.setText(carIndex);
                tv_vehicle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            else if(journey.getTransportation()==2) {
                String carIndex = "Skytrain";
                tv_vehicle.setText(carIndex);
                tv_vehicle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            else if(journey.getTransportation()==3) {
                String carIndex = "Walk/Bike";
                tv_vehicle.setText(carIndex);
                tv_vehicle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }


            TextView tv_emission = new TextView(this);
            if(flag == 0) {
                tv_emission.setText(String.valueOf("" + journey.getEmission()));
            }
            else{
                TextView text = (TextView)findViewById(R.id.tb_emission);
                text.setText("Laptop-hrs");
                tv_emission.setText(String.valueOf("" + journey.getEmission()/ laptopHours)+"Laptop-hrs");
            }
            tv_emission.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            TableRow tr = new TableRow(this);
            tr.addView(tv_date);
            tr.addView(tv_name);
            tr.addView(tv_distance);
            tr.addView(tv_vehicle);
            tr.addView(tv_emission);
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);
            tr.setLayoutParams(lp);
            tl.addView(tr, lp);
        }
    }
*/

    private void setupGraphBtn() {
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.graphBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = GraphActivity.makeIntent(EmissionActivity.this);
                intent.putExtra("flag",flag);
                startActivity(intent);
            }
        });
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}
