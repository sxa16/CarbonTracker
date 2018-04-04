package com.sfu.aqua.carbontracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.antonionicolaspina.revealtextview.RevealTextView;
import com.sfu.aqua.carbontracker.models.EnlargeAndBlurAnimation;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView txtvw_welcome = (TextView) findViewById(R.id.txtvw_welcome);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        txtvw_welcome.setTypeface(custom_font);
        //txtvw_welcome.setShadowLayer(22, 1, 1, Color.BLACK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((RevealTextView) findViewById(R.id.txtvw_welcome)).setAnimatedText(getString(R.string.carbon) + "\n" + getString(R.string.tracker));
            }
        }, 1);

        setupNextBtn();
        EnlargeAndBlurAnimation.changeAlphaThroughTime(0f, 1f, 5000, findViewById(R.id.btn_skip));

        ImageView imgV_bg = (ImageView) findViewById(R.id.imgV_bg);
        ImageView imgV_bg_blur = (ImageView) findViewById(R.id.imgV_bg_blur);
        EnlargeAndBlurAnimation.enlargeAndBlurBackgroundGradually(this, imgV_bg, imgV_bg_blur, 1f, 1.22f, 4321, 0.3f, 1f, 47f, 3333, 1.40f, 7777);
    }

    private void setupNextBtn() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.btn_skip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

}
