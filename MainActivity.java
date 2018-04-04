package com.sfu.aqua.carbontracker;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.sfu.aqua.carbontracker.models.EnlargeAndBlurAnimation;
import com.sfu.aqua.carbontracker.models.Day;
import com.sfu.aqua.carbontracker.models.NotificationReceiver;
import com.sfu.aqua.carbontracker.models.Singleton;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final double laptopHours = .012;
    private boolean hasData = false;
    int unitFlag;   // 0 = co2 KG  1 is other;
    private boolean firstRunFlag = true;

    int[] journeyTipsRepeat = new int[8];
    int[] utilityTipsRepeat = new int[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Singleton.firstrun) {
            GetAllSettings();
            Singleton.firstrun = false;
        }

        setContentView(R.layout.activity_main);

        //TextView group = (TextView)findViewById(R.id.groupName);
        //group.setTextColor(Color.WHITE);
        setupAbout();
        extractFlag();
        hideAlltext();
        setupButton();
        setupRepeatList();
        setupUnitChange();
        setupTipText();
        setupNextTips();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_leave);
        TextView txtvw_welcome = (TextView) findViewById(R.id.title);
        txtvw_welcome.setTextColor(Color.WHITE);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        Typeface UI_font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        TextView next_tip = (TextView) findViewById(R.id.tipText);
        next_tip.setTextColor(Color.WHITE);
        next_tip.setTypeface(UI_font);

        Button tip = (Button) findViewById(R.id.nextTip);
        Button about = (Button) findViewById(R.id.button);
        tip.setTypeface(UI_font);
        about.setTypeface(UI_font);
        tip.setTextColor(Color.WHITE);
        txtvw_welcome.setTypeface(custom_font);
        //TextView name = (TextView)findViewById(R.id.groupName);
        // name.setTypeface(custom_font);
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0f, 1f, 5000, findViewById(R.id.title));
        ////EnlargeAndBlurAnimation.changeAlphaThroughTime(0f, 1f, 7000, findViewById(R.id.groupName));
        //EnlargeAndBlurAnimation.changeAlphaThroughTime(0f, 1f, 5000, findViewById(R.id.imageView9));
    }
    @Override
    public void onBackPressed() {
        SaveAllSettings();
        this.finishAffinity();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveAllSettings();
    }


    private void setupNotification() {
        Calendar alarmCal = Calendar.getInstance();
        alarmCal.set(Calendar.HOUR_OF_DAY, 21); // Issue notification @ 9:00
        alarmCal.set(Calendar.MINUTE, 0);
        alarmCal.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void extractFlag() {
        if(firstRunFlag == true) {
            Intent intent = getIntent();
            unitFlag = intent.getIntExtra("flag", 0);
            Log.i("IN SELECT", " " + unitFlag);
        }
        //Intent intent = getIntent();
        //unitFlag=intent.getIntExtra("flag",0);
        //Log.i("IN SELECT"," "+unitFlag);
    }

    private void setupUnitChange() {
        Button btn = (Button) findViewById(R.id.unitBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitFlag == 0){
                    unitFlag = 1;
                    Log.i("my app", "Change units to laptop hours");
                }
                else{
                    unitFlag = 0;
                    Log.i("my app", "Change units to normal CO2 KG");
                }
            }
        });
        //SharedPreferences prefs = getSharedPreferences("The unit type", MODE_PRIVATE);
        //SharedPreferences.Editor editor = prefs.edit();
        //editor.putInt("unitType",unitFlag);
    }


    private void setupButton() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.action));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.utility));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.road));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.trans));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.emission));


        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowerButton)
                .build();
        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectUtilities.makeIntent(MainActivity.this);
                intent.putExtra("unitFlag",unitFlag);
                startActivity(intent);
            }
        });
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = JourneysActivity.makeIntent(MainActivity.this);
                intent.putExtra("unitFlag",unitFlag);
                startActivity(intent);
            }
        });
        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectTransportation.makeIntent(MainActivity.this);
                intent.putExtra("unitFlag",unitFlag);
                startActivity(intent);
            }
        });
        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = EmissionActivity.makeIntent(MainActivity.this);
                intent.putExtra("unitFlag",unitFlag);
                startActivity(intent);
            }
        });

        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
                showAlltext();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
                hideAlltext();

            }
        });

    }

    private void hideAlltext() {
        TextView utility = (TextView) findViewById(R.id.utility);
        TextView journey = (TextView) findViewById(R.id.journey);
        TextView transportation = (TextView) findViewById(R.id.transportation);
        TextView emission = (TextView) findViewById(R.id.emission);
        //utility.setText(" ");
        EnlargeAndBlurAnimation.changeAlphaThroughTime(1f, 0f, 1500, findViewById(R.id.utility));
        //journey.setText(" ");
        EnlargeAndBlurAnimation.changeAlphaThroughTime(1f, 0f, 1500, findViewById(R.id.journey));
        //transportation.setText(" ");
        EnlargeAndBlurAnimation.changeAlphaThroughTime(1f, 0f, 1500, findViewById(R.id.transportation));
        //emission.setText(" ");
        EnlargeAndBlurAnimation.changeAlphaThroughTime(1f, 0f, 1500, findViewById(R.id.emission));
    }

    private void showAlltext() {
        TextView utility = (TextView) findViewById(R.id.utility);
        TextView journey = (TextView) findViewById(R.id.journey);
        TextView transportation = (TextView) findViewById(R.id.transportation);
        TextView emission = (TextView) findViewById(R.id.emission);
        Typeface UI_font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        utility.setTypeface(UI_font);
        utility.setTextSize(15);
        utility.setTextColor(Color.WHITE);
        utility.setText("Add a utility");
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0f, 1f, 1500, findViewById(R.id.utility));
        journey.setText("View current journeys");
        journey.setTextSize(15);
        journey.setTypeface(UI_font);
        journey.setTextColor(Color.WHITE);
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0.1f, 1f, 1500, findViewById(R.id.journey));
        transportation.setText("Select a transportation");
        transportation.setTypeface(UI_font);
        transportation.setTextSize(15);
        transportation.setTextColor(Color.WHITE);
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0.2f, 1f, 1500, findViewById(R.id.transportation));
        emission.setText("View current emission");
        emission.setTypeface(UI_font);
        emission.setTextSize(15);
        emission.setTextColor(Color.WHITE);
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0.3f, 1f, 1500, findViewById(R.id.emission));
    }

    private void GetAllSettings() {
//        System.out.println("========================");
//        hasData = true;
        SharedPreferences sharedPreferences = getSharedPreferences("ran213", Context.MODE_PRIVATE);
        int extractedFlag = sharedPreferences.getInt("unitType",0);
        unitFlag = extractedFlag;
        firstRunFlag = false;
        Set<String> buffer;
        buffer = sharedPreferences.getStringSet("carList", new HashSet<String>());
        Singleton.setCarListFromString(buffer.toArray(new String[buffer.size()]));

        buffer = sharedPreferences.getStringSet("routeList", new HashSet<String>());
        Singleton.setRouteListFromString(buffer.toArray(new String[buffer.size()]));

        /*
        buffer = sharedPreferences.getStringSet("journeyList", new HashSet<String>());
        Singleton.setJourneyListFromString(buffer.toArray(new String[buffer.size()]));

        buffer = sharedPreferences.getStringSet("emissionList", new HashSet<String>());
        Singleton.setEmissionListFromString(buffer.toArray(new String[buffer.size()]));
        */

        buffer = sharedPreferences.getStringSet("electList", new HashSet<String>());
        Singleton.setElectricityListFromString(buffer.toArray(new String[buffer.size()]));

        buffer = sharedPreferences.getStringSet("gasList", new HashSet<String>());
        Singleton.setGasListFromString(buffer.toArray(new String[buffer.size()]));

       /*
       buffer = sharedPreferences.getStringSet("dayList", new HashSet<String>());
       Singleton.setDayListFromString(buffer.toArray(new String[buffer.size()]));
       */
    }


    private Set<String> arrayToSet(String arr[]) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, arr);
        return set;
    }

    private void SaveAllSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("ran213", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("unitType",unitFlag);
        editor.putStringSet("carList", arrayToSet(Singleton.getCarListAsString()));
        editor.putStringSet("routeList", arrayToSet(Singleton.getRouteListAsString()));
        //editor.putStringSet("journeyList", arrayToSet(Singleton.getJourneyListAsString()));
        editor.putStringSet("emissionList", arrayToSet(Singleton.getEmissionListAsString()));
        editor.putStringSet("electList", arrayToSet(Singleton.getElectricityListAsString()));
        editor.putStringSet("gasList", arrayToSet(Singleton.getGasListAsString()));
        //editor.putStringSet("dayList", arrayToSet(Singleton.getDayListAsString()));
        editor.commit();
    }


    private void setupNextTips() {
        Button next = (Button) findViewById(R.id.nextTip);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupTipText();
            }
        });
    }

    private void setupRepeatList() {
        SharedPreferences prefs = this.getSharedPreferences("AppPref", MODE_PRIVATE);
        if (prefs.contains("JourneyRepeats" + 0) || prefs.contains("UtilityRepeats" + 0)) {
            for (int i = 0; i < 8; i++) {
                journeyTipsRepeat[i] = prefs.getInt("JourneyRepeats" + i, 0);
                utilityTipsRepeat[i] = prefs.getInt("UtilityRepeats" + i, 0);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                journeyTipsRepeat[i] = 0;
                utilityTipsRepeat[i] = 0;
            }
        }
    }

    private void setupTipText() {
        TextView textView = (TextView) findViewById(R.id.tipText);
        Calendar calendar = Calendar.getInstance();

        Day today = Singleton.dayList.getDay(calendar);
        if(unitFlag ==0) {
            if (today.getTransportEmissions() == 0 && today.getUtilityEmission() == 0) {
                textView.setText(" ");
            } else if (today.getTransportEmissions() >= today.getUtilityEmission()) {
                int min = 0;
                for (int i = 0; i < 8; i++) {
                    if (journeyTipsRepeat[i] < journeyTipsRepeat[min])
                        min = i;
                }
                String[] tips = getResources().getStringArray(R.array.Journey_tips);
                textView.setText(getString(R.string.you_have_generated)
                        + String.format(Locale.CANADA, "%.2f", today.getTransportEmissions())
                        +  " " +getString(R.string.co2_from_trips) +
                        tips[min]);
                journeyTipsRepeat[min] += 1;
            } else {
                int min = 0;
                for (int i = 0; i < 8; i++) {
                    if (utilityTipsRepeat[i] < utilityTipsRepeat[min])
                        min = i;
                }
                String[] tips = getResources().getStringArray(R.array.Utility_tips);
                textView.setText(getString(R.string.you_have_generated)
                        + String.format(Locale.CANADA, "%.2f", today.getUtilityEmission())
                        +  " " +getString(R.string.co2_from_utility_use) +
                        tips[min]);
                utilityTipsRepeat[min] += 1;
            }
        }
        else{
            if (today.getTransportEmissions() == 0 && today.getUtilityEmission() == 0) {
                textView.setText(" ");
            } else if (today.getTransportEmissions() >= today.getUtilityEmission()) {
                int min = 0;
                for (int i = 0; i < 8; i++) {
                    if (journeyTipsRepeat[i] < journeyTipsRepeat[min])
                        min = i;
                }
                String[] tips = getResources().getStringArray(R.array.Journey_tips);
                textView.setText(getString(R.string.you_have_generated)
                        + String.format(Locale.CANADA, "%.2f", today.getTransportEmissions()/ laptopHours)
                        +  " " +getString(R.string.laptop_from_trips) +
                        tips[min]);
                journeyTipsRepeat[min] += 1;
            } else {
                int min = 0;
                for (int i = 0; i < 8; i++) {
                    if (utilityTipsRepeat[i] < utilityTipsRepeat[min])
                        min = i;
                }
                String[] tips = getResources().getStringArray(R.array.Utility_tips);
                textView.setText(getString(R.string.you_have_generated)
                        + String.format(Locale.CANADA, "%.2f", today.getUtilityEmission()/laptopHours)
                        +  " " + getString(R.string.laptop_from_utility) +
                        tips[min]);
                utilityTipsRepeat[min] += 1;
            }
        }
        SharedPreferences pref = this.getSharedPreferences("AppPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for (int i = 0; i < 8; i++) {
            editor.putInt("JourneyRepeats" + i, journeyTipsRepeat[i]);
            editor.putInt("UtilityRepeats" + i, utilityTipsRepeat[i]);
        }
        editor.apply();
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private void setupAbout() {

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });


    }
}