package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.Car;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.sfu.aqua.carbontracker.R.id.nickname;

public class AddCarActivity extends AppCompatActivity {

    private static List<Car> carList = new ArrayList();
    Spinner make;
    Spinner year;
    Spinner model;
    Spinner checkSpin;
    private int lineCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
        setupEditText();
        make = (Spinner)findViewById(R.id.make);
        year = (Spinner)findViewById(R.id.year);
        model = (Spinner)findViewById(R.id.model);
        checkSpin = (Spinner)findViewById(R.id.checkSpin);
        changeFont((TextView) findViewById(R.id.textView2));
        changeFont((TextView) findViewById(R.id.textView3));
        changeFont((TextView) findViewById(R.id.textView4));
        changeFont((TextView) findViewById(R.id.textView5));
        changeFont((TextView) findViewById(R.id.add));
        importData();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_car, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                super.onBackPressed();
                break;
            case R.id.home:
                Intent intent = new Intent(AddCarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ok:
                RadioGroup group = (RadioGroup) findViewById(R.id.rdgroup);
                RadioButton icon_one = (RadioButton) findViewById(R.id.icon_one);
                RadioButton icon_two = (RadioButton) findViewById(R.id.icon_two);
                RadioButton icon_three = (RadioButton) findViewById(R.id.icon_three);
                RadioButton icon_four = (RadioButton) findViewById(R.id.icon_four);
                RadioButton icon_five = (RadioButton) findViewById(R.id.icon_five);
                int car_id=R.drawable.car_one;
                if (icon_one.isChecked()) {
                    car_id = R.drawable.car_one;
                }else if (icon_two.isChecked()) {
                    car_id = R.drawable.car_two;
                }else if (icon_three.isChecked()) {
                    car_id = R.drawable.car_three;
                }else  if (icon_four.isChecked()) {
                    car_id = R.drawable.car_four;
                }else if (icon_five.isChecked()) {
                    car_id = R.drawable.car_five;
                }

                EditText editText=(EditText)findViewById(R.id.nickname);
                String nickName=editText.getText().toString();
                String strname = make.getSelectedItem().toString();
                String stryear = year.getSelectedItem().toString();
                String strmodel = model.getSelectedItem().toString();

                Log.i("TAG", "make " + strname +" year " +stryear);
                EditText nick = (EditText) findViewById(nickname);
                String theName = nick.getText().toString();
                Intent intent_add = new Intent();
                intent_add.putExtra("theyear",stryear);
                intent_add.putExtra("themake",strname);
                intent_add.putExtra("thenick",theName);
                intent_add.putExtra("themodel",strmodel);
                intent_add.putExtra("theicon",car_id);

                List<String> carCheck = new ArrayList<>();
                InputStream dataStream = getResources().openRawResource(R.raw.vehicles);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(dataStream, Charset.forName("UTF-8"))
                );
                String line = "";
                try {
                    while ( (line = reader.readLine()) != null){
                        String[] tokens = line.split(",");
                        if(Integer.parseInt(stryear)==Integer.parseInt(tokens[0]) && strname.equals(tokens[1]) && strmodel.equals(tokens[2])){
                            intent_add.putExtra("strFuelType",tokens[5]);
                            intent_add.putExtra("HighwayMPG",tokens[6]);
                            intent_add.putExtra("CityMPG",tokens[7]);
                        }
                    }
                } catch (IOException e) {
                    Log.wtf("Singleton", "Error rea" +
                            "ding data file on line " + lineCounter, e);
                    e.printStackTrace();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,carCheck);
                checkSpin.setAdapter(adapter);

                if ((theName.length() != 0)&(icon_one.isChecked()||icon_two.isChecked()||icon_three.isChecked()||icon_four.isChecked()||icon_five.isChecked())){
                    setResult(Activity.RESULT_OK,intent_add);
                    finish();
                }
                else
                    Toast.makeText(AddCarActivity.this, "Please enter a name/Please select a car icon.", Toast.LENGTH_SHORT)
                            .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEditText() {
        Intent intent=getIntent();
        String name=intent.getStringExtra("editNickname");
        EditText editName=(EditText)findViewById(R.id.nickname);
        editName.setText(name);

    }

    private List<Integer> caryear = new ArrayList<>();
    private List<String> carmake = new ArrayList<>();
    private List<String> carmodel = new ArrayList<>();

    public void importData() {

        InputStream dataStream = getResources().openRawResource(R.raw.vehicles);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(dataStream, Charset.forName("UTF-8"))
        );

        String line = "";

        try {
            while ( (line = reader.readLine()) != null){
                // Split by ','
                String[] tokens = line.split(",");
                //Read the data
                Car myCar = new Car();

                myCar.setNickname(""); //empty nickname because this car is for data
                myCar.setYear(Integer.parseInt(tokens[0]));
                if (!carmake.contains(tokens[1])) {
                    carmake.add(tokens[1]);
                }
                myCar.setMake(tokens[1]);

                myCar.setModel(tokens[2]);
                myCar.setTransmission(tokens[3]);
                myCar.setDisplacement(Double.parseDouble(tokens[4]));
                myCar.setFuelType(tokens[5]);
                myCar.setHighwayMPG(Integer.parseInt(tokens[6]));
                myCar.setCityMPG(Integer.parseInt(tokens[7]));
                carList.add(myCar);
                lineCounter++;
            }
        } catch (IOException e) {
            Log.wtf("Singleton", "Error reading data file on line " + lineCounter, e);
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,carmake){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextColor(Color.BLACK);
                ((TextView) v).setTextSize(20);
                return v;
            }};
        make.setAdapter(adapter);
        sendToSpin();

    }

    private void sendToSpin() {

        make.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                final List<String> newcarmodel = new ArrayList<>();
                final String strname = make.getSelectedItem().toString();
                for (int x =0; x<lineCounter; x++)
                {
                    Car result = carList.get(x);

                    if(result.getMake().equals(strname) && !newcarmodel.contains(result.getModel())){
                        newcarmodel.add(result.getModel());
                    }

                }
                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,newcarmodel){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextColor(Color.BLACK);
                        ((TextView) v).setTextSize(20);
                        return v;
                    }};
                model.setAdapter(adapter3);


                model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        List<Integer> newcaryear = new ArrayList<>();
                        String strmodel = model.getSelectedItem().toString();
                        for (int y =0; y<lineCounter; y++)
                        {
                            Car result = carList.get(y);
                            if(result.getMake().equals(strname) && result.getModel().equals(strmodel) && !newcaryear.contains(result.getYear())){
                                newcaryear.add(result.getYear());
                            }
                        }
                        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(getApplicationContext(),android.R.layout.simple_spinner_item,newcaryear){
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
                                ((TextView) v).setTypeface(externalFont);
                                ((TextView) v).setTextColor(Color.BLACK);
                                ((TextView) v).setTextSize(20);
                                return v;
                            }};
                        year.setAdapter(adapter2);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Log.d("THE APP", "HELLO");
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context,AddCarActivity.class);
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

    }
