package com.sfu.aqua.carbontracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    public static final double laptopHours = .012;
    double emission[];
    String routes[];
    int flag;

    public static Intent makeIntent(Context context) {
        return new Intent(context, GraphActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
        changeFont((TextView) findViewById(R.id.textView));
        extractFlag();
        setupList();
        setupPieChart();
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
                Intent intent = new Intent(GraphActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        Log.i("IN SELECT", " " + flag);
    }

    private void setupList() {
        int numRoutes = Singleton.routeList.size();
        routes = new String[numRoutes];
        emission = new double[Singleton.routeList.size()];
        for (int i = 0; i < numRoutes; i++) {
            routes[i] = Singleton.routeList.get(i).getName();
            if(flag == 0) {
                emission[i] = Singleton.routeList.get(i).getEmission();
            }
            else{
                emission[i] = Singleton.routeList.get(i).getEmission()/ laptopHours;
            }
        }
    }

    private void setupPieChart() {
        if (routes.length > 0) {
            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < routes.length; i++)
                pieEntries.add(new PieEntry((float)emission[i], routes[i]));
            PieDataSet dataSet = new PieDataSet(pieEntries,getString(R.string.route_names));
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(16);

            //get the chart
            PieChart chart = (PieChart) findViewById(R.id.chart);
            chart.setData(data);
            chart.animateY(1000);

            Description description = new Description();
            if (flag == 0){
                description.setText(getString(R.string.emission_data));
            }
            else{
                description.setText(getString(R.string.laptop_data));
            }
            description.setTextSize(16);
            chart.setDescription(description);

            chart.invalidate();

        }
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);}
}
