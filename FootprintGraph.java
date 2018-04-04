package com.sfu.aqua.carbontracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.R;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class FootprintGraph extends AppCompatActivity {
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footprint_graph);
        extractFlag();
        setupGraph();
        changeFont((TextView) findViewById(R.id.textView23));
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
                Intent intent = new Intent(FootprintGraph.this, MainActivity.class);
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
    private void setupGraph() {
        FloatingActionButton graph = (FloatingActionButton) findViewById(R.id.btn_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup group = (RadioGroup) findViewById(R.id.rdgroup);
                RadioButton single = (RadioButton) findViewById(R.id.rd_single);
                RadioButton multiple = (RadioButton) findViewById(R.id.rd_multiple);
                if (single.isChecked()) {
                    Intent intent = new Intent(FootprintGraph.this, PieGraph.class);
                    intent.putExtra("flag",flag);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FootprintGraph.this, BarGraph.class);
                    intent.putExtra("flag",flag);
                    startActivity(intent);
                }
            }

        });
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}
