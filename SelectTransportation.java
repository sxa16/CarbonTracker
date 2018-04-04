package com.sfu.aqua.carbontracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SelectTransportation extends AppCompatActivity {
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tranportation);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        TextView next_tip=(TextView) findViewById(R.id.textView9);
        next_tip.setTypeface(custom_font);
        setupByCarBtn();
        setupByBusBtn();
        setupBySkytrainBtn();
        setupWalkBtn();
        extractFlag();
    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("unitFlag",flag);
        Log.i("IN SELECT transport", " " + flag);
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
                Intent intent = new Intent(SelectTransportation.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWalkBtn() {
        FloatingActionButton button=(FloatingActionButton)findViewById(R.id.walk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=SelectRouteActivity.makeIntent(SelectTransportation.this);
                intent.putExtra("TRANSPORTATION",3);
                intent.putExtra("flag",flag);
                startActivity(intent);
            }
        });
    }

    private void setupBySkytrainBtn() {
        FloatingActionButton button=(FloatingActionButton)findViewById(R.id.bySkytrain);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=SelectRouteActivity.makeIntent(SelectTransportation.this);
                intent.putExtra("TRANSPORTATION",2);
                intent.putExtra("flag",flag);
                startActivity(intent);
            }
        });

    }

    private void setupByBusBtn() {
        FloatingActionButton button=(FloatingActionButton)findViewById(R.id.byBus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=SelectRouteActivity.makeIntent(SelectTransportation.this);
                intent.putExtra("TRANSPORTATION",1);
                intent.putExtra("flag",flag);
                startActivity(intent);
            }
        });
    }

    private void setupByCarBtn() {
        FloatingActionButton button=(FloatingActionButton)findViewById(R.id.byCar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("TRANSPORTATION",0);
                //intent.putExtra("flag",flag);
                Intent intent1= SelectCarActivity.makeIntent(SelectTransportation.this);
                intent1.putExtra("flag",flag);
                startActivity(intent1);
            }
        });

    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectTransportation.class);
    }
}
