package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.Car;
import com.sfu.aqua.carbontracker.models.Journey;
import com.sfu.aqua.carbontracker.models.Route;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.text.SimpleDateFormat;

public class EditJourneyActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSING_ROUTE = 1001;
    public static final int REQUEST_CODE_CHOOSING_CAR = 1002;
    int transportationMode;
    int CarIndex = -1;
    int RouteIndex = -1;
    Calendar calendar = Calendar.getInstance();

    public static Intent makeIntent(Context context) {
        return new Intent(context, EditJourneyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey2);
        changeFont((TextView) findViewById(R.id.textView25));
        changeFont((TextView) findViewById(R.id.textView11));
        changeFont((TextView) findViewById(R.id.dateText));
        setupChooseDateBtn();
        setupRadioBtn();
        setupChooseCarBtn();
        setupChooseRouteBtn();
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
                Intent intent = new Intent(EditJourneyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ok:
                Intent intent_edit = getIntent();
                int edit = intent_edit.getIntExtra("EDIT_INDEX", 0);
                if (RouteIndex == -1)
                    Toast.makeText(EditJourneyActivity.this, "Please choose a route", Toast.LENGTH_SHORT).show();
                else {
                    Route route = Singleton.routeList.get(RouteIndex);
                    double highway = route.getHighway();
                    double city = route.getCity();
                    if (transportationMode == 0) {
                        if (CarIndex == -1)
                            Toast.makeText(EditJourneyActivity.this, "Please choose a car", Toast.LENGTH_SHORT).show();
                        else {
                            Intent intent1 = new Intent();
                            double emission = Singleton.carList.get(CarIndex).getEmissions(city, highway);
                            Singleton.changeJourney(edit, new Journey(calendar  , Singleton.carList.get(CarIndex), route, emission));
                            setResult(Activity.RESULT_OK, intent1);
                            finish();
                        }
                    } else if (transportationMode == 1 || transportationMode == 2 || transportationMode == 3) {
                        Intent intent1 = new Intent();
                        double distance = highway + city;
                        double emission = calculate(transportationMode, distance);
                        Singleton.changeJourney(edit, new Journey(calendar  , Singleton.routeList.get(RouteIndex), emission, transportationMode));

                        setResult(Activity.RESULT_OK, intent1);
                        finish();
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupChooseDateBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.editDate);
        Intent intent = getIntent();
        TextView display = (TextView) findViewById(R.id.dateText);
        int editIndex = intent.getIntExtra("EDIT_INDEX", 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        display.setText("Date: " + dateFormat.format(Singleton.journeyList.get(editIndex).getDate().getTime()));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditJourneyActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            TextView display = (TextView) findViewById(R.id.dateText);
            calendar.set(year, month, dayOfMonth);
            display.setText("Date: " + dayOfMonth + "/" + (month + 1) + "/" + year);
        }
    };


    private double calculate(int trans, double distance) {
        double emission = 0;
        if (trans == 3)
            emission = 0;
        else if (trans == 1)
            emission = distance * 0.089;
        else if (trans == 2)
            emission = distance * 0.02;
        return emission;

    }


    private void setupChooseRouteBtn() {
        Button button = (Button) findViewById(R.id.chooseRoute);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectRouteActivity.makeIntent(EditJourneyActivity.this);
                intent.putExtra("EditJourney", 1);
                startActivityForResult(intent, REQUEST_CODE_CHOOSING_ROUTE);
            }
        });
    }

    private void setupRadioBtn() {
        RadioGroup group = (RadioGroup) findViewById(R.id.editTrans);
        String[] trans = getResources().getStringArray(R.array.EDIT_TRANSPORTATION);
        for (int i = 0; i < trans.length; i++) {
            final String transportation = trans[i];
            RadioButton button = new RadioButton(this);
            button.setText(transportation);
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportationMode = finalI;
                }
            });
            group.addView(button);
            Intent intent = getIntent();
            int transport = intent.getIntExtra("TRANS", 0);
            if (i == transport) {
                button.setChecked(true);
            }
        }
    }

    private void setupChooseCarBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.chooseCar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transportationMode == 0) {
                    Intent intent = SelectCarActivity.makeIntent(EditJourneyActivity.this);
                    intent.putExtra("EditJourney", 1);
                    startActivityForResult(intent, REQUEST_CODE_CHOOSING_CAR);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSING_CAR:
                if (resultCode == Activity.RESULT_OK) {
                    CarIndex = data.getIntExtra("CarIndex", 0);
                    TextView textView = (TextView) findViewById(R.id.currentCar);
                    textView.setText("Car: " + Singleton.carList.get(CarIndex).getNickname());

                }
        }
        switch (requestCode) {
            case REQUEST_CODE_CHOOSING_ROUTE:
                if (resultCode == Activity.RESULT_OK) {
                    RouteIndex = data.getIntExtra("RouteIndex", 0);
                    TextView textView = (TextView) findViewById(R.id.currentRoute);
                    textView.setText("Route: " + Singleton.routeList.get(RouteIndex).getName());
                }
        }
    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

}
