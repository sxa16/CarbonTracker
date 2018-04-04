package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.sfu.aqua.carbontracker.models.Singleton;

public class SelectUtilities extends AppCompatActivity {
    public static final int REQUEST_CODE_GET_MESSAGE = 1105;
    public static final int REQUEST_CODE_GET_MESSAGE2 = 1109;
    int indexGas = 0;
    int indexSelect = 0;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_utilities);
        changeFont((TextView) findViewById(R.id.textView17));
        changeFont((TextView) findViewById(R.id.textView18));
        extract();
        setupMSG();
        populateListView();
        registerClickEdit();
        DeleteButton();
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
                Intent intent = new Intent(SelectUtilities.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void extract() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("unitFlag",0);
        Log.i("IN SELECT"," "+flag);
    }

    private void registerClickEdit() {
        ListView GasListView = (ListView) findViewById(R.id.gasView);
        ListView ElectListView = (ListView) findViewById(R.id.ElecView);
        GasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = AddUtilities.makeIntent(SelectUtilities.this);

                    intent.putExtra("gasIndex", position);
                    intent.putExtra("theUtil","Gas");

                    indexGas = position;
                    startActivityForResult(intent, REQUEST_CODE_GET_MESSAGE2);
            }
        });

        ElectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Singleton.electList.getElectricity(position).isDeleted()) {
                    Intent intent = AddUtilities.makeIntent(SelectUtilities.this);

                    intent.putExtra("electIndex", position);
                    intent.putExtra("theUtil", "Electricity");

                    indexSelect = position;
                    startActivityForResult(intent, REQUEST_CODE_GET_MESSAGE2);
                }
            }
        });
        DeleteButton();
    }

    private void DeleteButton() {
        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.Del);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Singleton.gasList.count()==0 && Singleton.electList.count()==0){
                    Toast.makeText(SelectUtilities.this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SelectUtilities.this, R.string.tap_bills_to_delete, Toast.LENGTH_SHORT)
                            .show();
                    ListView list = (ListView) findViewById(R.id.gasView);
                    ListView list2 = (ListView) findViewById(R.id.ElecView);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Singleton.dayList.removeGas(Singleton.gasList.getGas(position));
                            Singleton.gasList.deleteGas(position);
                            populateListView();
                            Toast.makeText(SelectUtilities.this, R.string.click_on_delete_to_finish, Toast.LENGTH_SHORT)
                                    .show();
                            FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.Del);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registerClickEdit();
                                }
                            });
                        }
                    });
                    list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Singleton.dayList.removeElectricity(Singleton.electList.getElectricity(position));
                            Singleton.electList.deleteElectricity(position);
                            populateListView();
                            Toast.makeText(SelectUtilities.this, R.string.click_on_delete_to_finish, Toast.LENGTH_SHORT)
                                    .show();
                            FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.Del);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registerClickEdit();
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private void populateListView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.gas_items,
                Singleton.gasList.getDescription(this));
        ListView list = (ListView) findViewById(R.id.gasView);
        list.setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this,
                R.layout.elect_items,
                Singleton.electList.getDescription(this));
        ListView list2 = (ListView) findViewById(R.id.ElecView);
        list2.setAdapter(adapter2);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            populateListView();
        }
    }

    private void setupMSG() {
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUtilities.makeIntent(SelectUtilities.this);
                intent.putExtra("theUtil", "newUtil");//set default utility
                intent.putExtra("electIndex", Singleton.electList.count());
                intent.putExtra("flag",flag);
                startActivityForResult(intent, REQUEST_CODE_GET_MESSAGE);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectUtilities.class);
    }

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}
