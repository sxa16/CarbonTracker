package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.Journey;
import com.sfu.aqua.carbontracker.models.Route;
import com.sfu.aqua.carbontracker.models.Singleton;

public class SelectRouteActivity extends AppCompatActivity {
    public static final double laptopHours = .012;
    private int index;
    private Calendar calendar = Calendar.getInstance();
    private double Emission;
    private int carPosition;
    private int itemPosition;
    int changeUnitFlag;
    private int trans;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectRouteActivity.class);
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
                Intent intent = new Intent(SelectRouteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);
        changeFont((TextView) findViewById(R.id.textView8));
        setupAddBtn();
        populateListView();
        registerClickCallback();
        setupDeleteBtn();
        extractUnitFlag();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
    }

    private void extractUnitFlag() {
        Intent intent = getIntent();
        changeUnitFlag = intent.getIntExtra("flag", 0);
        Log.i("IN SELECT", " " + changeUnitFlag);
    }

    private void setupDeleteBtn() {
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.deleteRoute);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Singleton.routeList.size() == 0) {
                    Toast.makeText(SelectRouteActivity.this, getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectRouteActivity.this, getString(R.string.tap_car_to_delete), Toast.LENGTH_SHORT)
                            .show();
                    ListView list = (ListView) findViewById(R.id.listViewRoutes);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Singleton.routeList.remove(position);
                            populateListView();
                            Toast.makeText(SelectRouteActivity.this, getString(R.string.click_on_delete_to_finish), Toast.LENGTH_SHORT)
                                    .show();
                            FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.deleteRoute);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registerClickCallback();
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    private void populateListView() {
        String[] myRouteList = getDescriptions();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.routelist,
                myRouteList) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextColor(Color.BLACK);
                ((TextView) v).setTextSize(20);
                return v;
            }
        };
        ListView list = (ListView) findViewById(R.id.listViewRoutes);
        list.setAdapter(adapter);

    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listViewRoutes);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                if (!Singleton.routeList.get(position).deleted()) {
                    Intent intent = getIntent();
                    int edit = intent.getIntExtra("EditJourney", 0);
                    if (edit == 0) {
                        Calendar date = Calendar.getInstance();
                        trans = intent.getIntExtra("TRANSPORTATION", 0);
                        double cityDriven = Singleton.routeList.get(position).getCity();
                        double highwayDriven = Singleton.routeList.get(position).getHighway();
                        double distance = cityDriven + highwayDriven;
                        itemPosition = position;
                        if (trans == 0) {
                            carPosition = intent.getIntExtra("CARINDEX", 0);
                            Emission = calculation(cityDriven, highwayDriven);
                            selectDate(Emission);
                            //Singleton.emissionList.add(Emission);
                            //Singleton.routeList.get(position).addEmission(Emission);
                            //Singleton.addJourney(new Journey(calendar, Singleton.carList.get(carPosition), Singleton.routeList.get(position), Emission));
                        } else if (trans == 1 || trans == 2 || trans == 3) {
                            Emission = calculate(trans, distance);
                            selectDate(Emission);
                            //Singleton.addJourney(new Journey(calendar, Singleton.routeList.get(position), Emission, trans));
                        }

                    } else if (edit == 1) {
                        Intent intent3 = getIntent();
                        intent3.putExtra("RouteIndex", position);
                        setResult(Activity.RESULT_OK, intent3);
                        finish();
                    }
                }
            }


        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Singleton.routeList.get(position).deleted()) {
                    Intent intent = AddRouteActivity.makeIntent(SelectRouteActivity.this);

                    intent.putExtra("editName", Singleton.routeList.get(position).getName());
                    index = position;
                    startActivityForResult(intent, 158);
                }
                return true;
            }
        });
        setupDeleteBtn();
    }

    private void selectDate(final double Emission) {
        final DatePickerDialog date = new DatePickerDialog(SelectRouteActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        date.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendar.set(date.getDatePicker().getYear(), date.getDatePicker().getMonth(), date.getDatePicker().getDayOfMonth());

                if (trans == 0) {
                    Singleton.emissionList.add(Emission);
                    Singleton.routeList.get(itemPosition).addEmission(Emission);
                    Singleton.addJourney(new Journey(calendar, Singleton.carList.get(carPosition), Singleton.routeList.get(itemPosition), Emission));
                } else if (trans == 1 || trans == 2 || trans == 3) {
                    Singleton.addJourney(new Journey(calendar, Singleton.routeList.get(itemPosition), Emission, trans));
                }
                if(Singleton.dayList.getDay(calendar).hasThisRoute(Singleton.routeList.get(itemPosition))){
                    Singleton.dayList.getDay(calendar).getRouteOfTheDay(Singleton.routeList.get(itemPosition)).addEmission(Emission);
                }
                else{
                    Singleton.dayList.getDay(calendar).getRouteListOfTheDay().add(Singleton.routeList.get(itemPosition));
                    Singleton.dayList.getDay(calendar).getRouteOfTheDay(Singleton.routeList.get(itemPosition)).addEmission(Emission);
                }
                if(changeUnitFlag == 0) {
                    ShowMessageBox(getString(R.string.your_emission) + Emission + "kg.", "Emission Calculation");
                }
                else{
                    ShowMessageBox(getString(R.string.your_emission) + Emission/ laptopHours + getString(R.string.laptop_hours), "Emission Calculation");

                }
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

    private float calculate(int trans, double distance) {
        float emission = 0;
        if (trans == 3)
            emission = 0;
        else if (trans == 1)
            emission = (float) (distance * 0.089);
        else if (trans == 2)
            emission = (float) (distance * 0.02);
        return emission;

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
                Intent intent = MainActivity.makeIntent(SelectRouteActivity.this);
                intent.putExtra("flag",changeUnitFlag);
                startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private float calculation(double cityDriven, double highwayDriven) {
        int position = getIntent().getIntExtra("CARINDEX", 0);
        return (float) Singleton.carList.get(position).getEmissions(cityDriven, highwayDriven);
    }

    private void setupAddBtn() {
        Intent intent1 = getIntent();
        final int trans = intent1.getIntExtra("TRANSPORTATION", 0);
        final int editJourney = intent1.getIntExtra("EditJourney", 0);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addRoute);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddRouteActivity.makeIntent(SelectRouteActivity.this);
                if (editJourney == 1) {
                    intent.putExtra("EDITING", 1);
                }
                intent.putExtra("Transportation_Mode", trans);
                startActivityForResult(intent, 95);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.i("App", "my app canceled");
            return;
        }
        String newName, inCity, onHighway;
        float city, highway;
        int carIndex;
        switch (requestCode) {
            case 95:
                newName = AddRouteActivity.getRouteName(data);
                inCity = AddRouteActivity.getInCity(data);
                onHighway = AddRouteActivity.getHighway(data);
                city = Float.parseFloat(inCity);
                highway = Float.parseFloat(onHighway);
                Singleton.routeList.add(new Route(newName, city, highway));
                populateListView();
                break;
            case 158:
                int pos = index;
                newName = AddRouteActivity.getRouteName(data);
                inCity = AddRouteActivity.getInCity(data);
                onHighway = AddRouteActivity.getHighway(data);
                city = Float.parseFloat(inCity);
                highway = Float.parseFloat(onHighway);
                Singleton.changeRoute(pos, new Route(newName, city, highway));
                populateListView();
                break;
        }
    }

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

    public String[] getDescriptions() {
        String[] descriptions = new String[Singleton.routeList.size()];
        int loopIndex = 0;
        int k = 0;
        while (loopIndex < Singleton.routeList.size()) {
            Route route = Singleton.routeList.get(loopIndex);
            if (!route.deleted()) {
                descriptions[k] = route.getName() + ": " + route.getCity() + " " + getString(R.string.km_in_city) + " " + route.getHighway() + " " + getString(R.string.km_on_highway);
                k++;
            }
            loopIndex++;
            //else {
            //    descriptions[i] = "";
            //}
            //i++;
        }
        return descriptions;
    }

}