package com.sfu.aqua.carbontracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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

import com.sfu.aqua.carbontracker.models.Journey;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JourneysActivity extends AppCompatActivity {
    private List<Journey> myJourneys = new ArrayList<Journey>();
    public static final int REQUEST_CODE_EDITING_JOURNEY = 222;
    public static final double laptopHours = .012;
    int flag;

    public static Intent makeIntent(Context context) {
        return new Intent(context, JourneysActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journeys);
        changeFont((TextView) findViewById(R.id.textForJourney));
        extractFlag();
        populateListView();
        registerClickCallback();
        setupGraphBtn();
        setupDeleteBtn();

    }

    private void extractFlag() {
        Intent intent = getIntent();
        flag = intent.getIntExtra("unitFlag", 0);
        Log.i("IN SELECT", " " + flag);
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
                Intent intent = new Intent(JourneysActivity.this, MainActivity.class);
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

    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);

    }

    private void setupGraphBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_graph);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JourneysActivity.this, FootprintGraph.class);
                intent.putExtra("flag", flag);
                startActivity(intent);
            }
        });
    }


    private void setupDeleteBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.deleteJourney);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Singleton.journeyList.size() == 0) {
                    Toast.makeText(JourneysActivity.this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JourneysActivity.this, R.string.tap_journey_to_delete, Toast.LENGTH_SHORT)
                            .show();
                    ListView list = (ListView) findViewById(R.id.Journey);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Singleton.journeyList.remove(position);
                            populateListView();
                            Toast.makeText(JourneysActivity.this, R.string.click_on_delete_to_finish, Toast.LENGTH_SHORT)
                                    .show();
                            Button btn = (Button) findViewById(R.id.deleteJourney);
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
        populateJourneyList();
        ArrayAdapter<Journey> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.Journey);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Journey> {
        public MyListAdapter() {
            super(JourneysActivity.this, R.layout.journey_view, myJourneys);
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            TextView makeText = (TextView) itemView.findViewById(R.id.txt_date);
            makeText.setText(dateFormat.format(currentJourney.getDate().getTime()));

            // Year:
            TextView yearText = (TextView) itemView.findViewById(R.id.txt_emission);
            if (flag == 0) {
                yearText.setText(String.format("%.2f", currentJourney.getEmission()) + "g ");
            } else {
                yearText.setText(String.format("%.2f", currentJourney.getEmission() / laptopHours) + " laptop hours ");
            }


            TextView modelText = (TextView) itemView.findViewById(R.id.txt_distance);
            modelText.setText(currentJourney.getDistance() + "km   ");


            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.Journey);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(JourneysActivity.this, "rrrr", Toast.LENGTH_SHORT).show();

                Intent intent = EditJourneyActivity.makeIntent(JourneysActivity.this);
                intent.putExtra("TRANS", Singleton.journeyList.get(position).getTransportation());
                intent.putExtra("EDIT_INDEX", position);

                startActivityForResult(intent, REQUEST_CODE_EDITING_JOURNEY);
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDITING_JOURNEY:
                if (resultCode == Activity.RESULT_OK) {
                    populateListView();
                }
        }
    }


}
