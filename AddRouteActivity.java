package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.Car;
import com.sfu.aqua.carbontracker.models.Journey;
import com.sfu.aqua.carbontracker.models.Route;
import com.sfu.aqua.carbontracker.models.Singleton;

public class AddRouteActivity extends AppCompatActivity {
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);
        changeFont((TextView) findViewById(R.id.routeName));
        changeFont((TextView) findViewById(R.id.textView5));
        changeFont((TextView) findViewById(R.id.textView6));
        changeFont((TextView) findViewById(R.id.textView7));
        changeFont((TextView) findViewById(R.id.textView19));
        setupEditText();
        setupAutoBtn();
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
                Intent intent = new Intent(AddRouteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ok:
                EditText routesName = (EditText) findViewById(R.id.editName);
                String name = routesName.getText().toString();
                EditText cityDriving = (EditText) findViewById(R.id.editCity);
                String inCity = cityDriving.getText().toString();
                EditText highwayDriving = (EditText) findViewById(R.id.editHighway);
                String highway = highwayDriving.getText().toString();

                boolean valid = true;
                if (name.length() <= 1) {
                    Toast.makeText(AddRouteActivity.this, "Please enter a name longer than one character", Toast.LENGTH_SHORT)
                            .show();
                    valid = false;
                }
                if (inCity.startsWith("-") || highway.startsWith("-")) {
                    Toast.makeText(AddRouteActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT)
                            .show();
                    valid = false;
                }
                if (inCity.isEmpty() & highway.isEmpty()) {
                    Toast.makeText(AddRouteActivity.this, "Highway and City cannot both be 0!", Toast.LENGTH_SHORT)
                            .show();
                    valid = false;
                }
                if (inCity.isEmpty() & (!highway.isEmpty())) {
                    Toast.makeText(AddRouteActivity.this, "City distance is defaulted to 0.", Toast.LENGTH_SHORT)
                            .show();
                    inCity = "0";
                    valid = true;
                }
                if (!inCity.isEmpty() & (highway.isEmpty())) {
                    Toast.makeText(AddRouteActivity.this, "Highway is defaulted to 0.", Toast.LENGTH_SHORT)
                            .show();
                    highway = "0";
                    valid = true;
                }

                if (valid) {
                    Intent intent_add = new Intent();
                    intent_add.putExtra("NAME", name);
                    intent_add.putExtra("IN CITY", inCity);
                    intent_add.putExtra("HIGHWAY", highway);
                    setResult(Activity.RESULT_OK, intent_add);
                    finish();
                }

                if (!valid) {
                    break;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupAutoBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.autoUse);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                int trans = intent.getIntExtra("Transportation_Mode", 0);
                EditText cityDriving = (EditText) findViewById(R.id.editCity);
                String inCity = cityDriving.getText().toString();
                EditText highwayDriving = (EditText) findViewById(R.id.editHighway);
                String highway = highwayDriving.getText().toString();
                boolean valid = true;
                if (inCity.isEmpty() || inCity.startsWith("-")) {
                    Toast.makeText(AddRouteActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT)
                            .show();
                    valid = false;
                }
                if (highway.isEmpty() || highway.startsWith("-")) {
                    Toast.makeText(AddRouteActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT)
                            .show();
                    valid = false;
                }
                if (valid) {
                    int editing = intent.getIntExtra("EDITING", 0);
                    if (editing == 0) {
                        double Emission = 0;

                        double cityDriven = Double.parseDouble(inCity);
                        double highwayDriven = Double.parseDouble(highway);
                        int pos = intent.getIntExtra("CARINDEX", 0);
                        if (trans == 0) {
                            Emission = Singleton.carList.get(pos).getEmissions(cityDriven, highwayDriven);
                            Singleton.emissionList.add(Emission);
                            Route route = new Route(" ", cityDriven, highwayDriven);
                            Car car = Singleton.carList.get(pos);
                            selectDate(Emission);
                            Journey journey = new Journey(calendar, car, route, Emission);
                            Singleton.addJourney(journey);
                        } else {
                            double distance = cityDriven + highwayDriven;
                            Emission = calculate(trans, distance);
                            Singleton.emissionList.add(Emission);
                            Route route = new Route(" ", cityDriven, highwayDriven);
                            selectDate(Emission);
                            Journey journey = new Journey(calendar, route, Emission, trans);
                            Singleton.addJourney(journey);
                        }

                    } else if (editing == 1) {
                        Toast.makeText(AddRouteActivity.this, "Please use a save route for editing a journey", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void selectDate(final double Emission) {
        final DatePickerDialog date = new DatePickerDialog(AddRouteActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        date.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar.set(date.getDatePicker().getYear(), date.getDatePicker().getMonth(), date.getDatePicker().getDayOfMonth());
                Singleton.dayList.getDay(calendar).addAutouseEmission(Emission);
                ShowMessageBox("Your emission is " + Emission + "kg.", "Emission Calculation");
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


    private void setupEditText() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("editName");
        EditText editName = (EditText) findViewById(R.id.editName);
        editName.setText(name);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, AddRouteActivity.class);
    }

    public static String getRouteName(Intent intent) {
        return intent.getStringExtra("NAME");
    }

    public static String getInCity(Intent intent) {
        return intent.getStringExtra("IN CITY");
    }

    public static String getHighway(Intent intent) {
        return intent.getStringExtra("HIGHWAY");
    }

    private void ShowMessageBox(String msgText, String title) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(title);
        LayoutInflater layInf = LayoutInflater.from(this);
        View view = layInf.inflate(R.layout.msgbox_image, null);
        dialog.setView(view);
        TextView msg_txt = (TextView) view.findViewById(R.id.msg_txt);
        msg_txt.setText(msgText);
        Button btn = (Button) view.findViewById(R.id.btn_ok_msg);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = MainActivity.makeIntent(AddRouteActivity.this);
                startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

    private float calculate(int trans, double distance) {
        float emission = 0;
        if (trans == 3)
            emission = 0;
        else if (trans == 1)
            emission = (float) (distance * 0.089);
        else if (trans == 2)
            emission = (float) (distance * 0.034);
        return emission;

    }

}
