package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfu.aqua.carbontracker.models.Car;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.util.ArrayList;
import java.util.List;

public class SelectCarActivity extends AppCompatActivity {
    private List<Car> myCars = new ArrayList<Car>();
    public static final int REQUEST_CODE_GETMESSAGE = 1103;
    int index = 0;
    int changeUnitFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
//        changeFont((TextView) findViewById(R.id.textView2));
        setupAddBtn();
        populateListView();
        registerClickCallback();
        setupDeleteBtn();
        extractUnitFlag();
    }

    private void extractUnitFlag() {
        Intent intent = getIntent();
        changeUnitFlag = intent.getIntExtra("flag", 0);
        Log.i("IN SELECT", " " + changeUnitFlag);
    }

    private void populateCarList() {
        myCars.clear();
        for (Car car : Singleton.carList) {
            if (!car.isDeleted()) {

                myCars.add(new Car(car.getNickname(), car.getYear(), car.getMake(), car.getModel(), car.getFuelType(), car.getCityMPG(), car.getHighwayMPG(),car.getIcon()));
            }
        }
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
                Intent intent = new Intent(SelectCarActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupAddBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addCar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddCarActivity.makeIntent(SelectCarActivity.this);
                startActivityForResult(intent, REQUEST_CODE_GETMESSAGE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GETMESSAGE:
                if (resultCode == Activity.RESULT_OK) {

                    String carMake = data.getStringExtra("themake");
                    String carYear = data.getStringExtra("theyear");
                    int year = Integer.parseInt(carYear);
                    String nickname = data.getStringExtra("thenick");
                    String carModel = data.getStringExtra("themodel");
                    String fuelType = data.getStringExtra("strFuelType");
                    String city = data.getStringExtra("CityMPG");
                    String highway = data.getStringExtra("HighwayMPG");
                    int icon = data.getIntExtra("theicon", R.id.icon_one);
                    //System.out.println("==========="+icon);
                    int cityMpg = Integer.parseInt(city);
                    int highwayMpg = Integer.parseInt(highway);
                    Singleton.carList.add(new Car(nickname, year, carMake, carModel, fuelType, cityMpg, highwayMpg, icon));
                    populateListView();
                    break;
                }
        }
        switch (requestCode) {
            case 1888:
                if (resultCode == Activity.RESULT_OK) {
                    int pos = index;
                    String carMake = data.getStringExtra("themake");
                    String carYear = data.getStringExtra("theyear");
                    int year = Integer.parseInt(carYear);
                    String nickname = data.getStringExtra("thenick");
                    String carModel = data.getStringExtra("themodel");
                    int icon = data.getIntExtra("theicon", R.id.icon_one);
                    String fuelType = data.getStringExtra("strFuelType");
                    int cityMpg = data.getIntExtra("CityMPG", 0);
                    int highwayMpg = data.getIntExtra("HighwayMPG", 0);
                    Singleton.changeCar(pos, new Car(nickname, year, carMake, carModel, fuelType, cityMpg, highwayMpg,icon));
                    populateListView();
                    break;
                }
        }

    }

    private void populateListView() {
        populateCarList();
        ArrayAdapter<Car> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.car);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Car> {
        public MyListAdapter() {
            super(SelectCarActivity.this, R.layout.item_view, myCars);
            //System.out.println("=======" + myCars.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            // Find the car to work with.
            Car currentCar = myCars.get(position);

            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_car);
            imageView.setImageResource(currentCar.getIcon());


            // Condition:
            TextView condtionText = (TextView) itemView.findViewById(R.id.txt_nickname);
            condtionText.setText(currentCar.getNickname());

            // Make:
            TextView makeText = (TextView) itemView.findViewById(R.id.txt_make);
            makeText.setText(currentCar.getMake());

            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.txt_year);
            yearText.setText("" + currentCar.getYear());

            TextView modelText = (TextView) itemView.findViewById(R.id.txt_model);
            modelText.setText("" + currentCar.getModel());



            return itemView;
        }
    }

    private void registerClickCallback() {

        ListView list = (ListView) findViewById(R.id.car);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                if (!Singleton.carList.get(position).isDeleted()) {
                    Intent intent = getIntent();
                    int editJourney = intent.getIntExtra("EditJourney",0);
                    if(editJourney == 0) {
                        Intent intent1 = SelectRouteActivity.makeIntent(SelectCarActivity.this);
                        intent1.putExtra("CARINDEX", position);
                        intent1.putExtra("flag", changeUnitFlag);
                        startActivity(intent1);
                    }
                    else if(editJourney == 1){
                        Intent intent3 = getIntent();
                        intent3.putExtra("CarIndex",position);
                        setResult(Activity.RESULT_OK, intent3);
                        finish();

                    }
                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Singleton.carList.get(position).isDeleted()) {
                    Intent intent = AddCarActivity.makeIntent(SelectCarActivity.this);

                    intent.putExtra("editNickname", Singleton.carList.get(position).getNickname());
                    index = position;
                    startActivityForResult(intent, 1888);
                }
                return true;
            }
        });
        setupDeleteBtn();
    }

    private void setupDeleteBtn() {
        FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.deleteCar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Singleton.carList.size() == 0) {
                    Toast.makeText(SelectCarActivity.this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectCarActivity.this, R.string.tap_car_to_delete, Toast.LENGTH_SHORT)
                            .show();
                    ListView list = (ListView) findViewById(R.id.car);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Singleton.carList.remove(position);
                            populateListView();
                            Toast.makeText(SelectCarActivity.this, R.string.click_on_delete_to_finish, Toast.LENGTH_SHORT)
                                    .show();
                            FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.deleteCar);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.deleteCar);
                                    //btn.setText("DELETE");
                                    registerClickCallback();
                                }
                            });

                        }
                    });
                }

            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectCarActivity.class);
    }

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }

    private void changeFont(Button btn) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/thin.otf"
        );
        btn.setTypeface(typeface);

    }

    /*public String[] getDescription() {
        String[] descriptions = new String[Singleton.carList.size()];
        int i = 0;
        for (Car car : Singleton.carList) {
            if (!car.isDeleted()) {
                descriptions[i] = car.getNickname() + "\n" +
                        car.getMake() + ", " + car.getModel() + ", " + car.getYear() + car.getIcon();
            } else {
                descriptions[i] = " ";
            }
            i++;
        }
        return descriptions;
    }*/


}

