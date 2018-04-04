package com.sfu.aqua.carbontracker;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView txtvw_link = (TextView) findViewById(R.id.txtvw_link);
        txtvw_link.setClickable(true);
        txtvw_link.setMovementMethod(LinkMovementMethod.getInstance());
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        txtvw_link.setText(Html.fromHtml("<a href='http://www.cs.sfu.ca/CourseCentral/276/bfraser/index.html'> CMPT 276 home-page </a>"));
        changeFont(txtvw_link);
        TextView txtvw_version = (TextView) findViewById(R.id.version_number);
        txtvw_version.setText("V"+ versionName +"\n" +"07/04/2017");
        changeFont(txtvw_version);
        TextView name = (TextView) findViewById(R.id.name);
        changeFont(name);
        TextView link = (TextView) findViewById(R.id.link1);
        link.setClickable(true);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        link.setText(Html.fromHtml("<a href='http://m.jzjdm.com/index/6buR55m955qE5omL5py66IOM5pmv.html'> Background link </a>\n"
        ));
        TextView link2 = (TextView) findViewById(R.id.link2);
        link2.setClickable(true);
        link2.setMovementMethod(LinkMovementMethod.getInstance());
        link2.setText(Html.fromHtml(
                "<a href='http://www.qhgzd0.biz/portfolio.html'> 'Add car' background image link </a>"+"\n"
        ));

        TextView link3 = (TextView) findViewById(R.id.link3);
        link3.setClickable(true);
        link3.setMovementMethod(LinkMovementMethod.getInstance());
        link3.setText(Html.fromHtml(
                "<a href='https://www.pinterest.com/zaratorresjimen/wallpapers/'> 'Add route' background image link </a>"
        ));

        TextView link4 = (TextView) findViewById(R.id.link4);
        link4.setClickable(true);
        link4.setMovementMethod(LinkMovementMethod.getInstance());
        link4.setText(Html.fromHtml(
                "<a href='https://es.pinterest.com/pin/328410997812254601/'> Graph background image link </a>"
        ));

        TextView link5 = (TextView) findViewById(R.id.link5);
        link5.setClickable(true);
        link5.setMovementMethod(LinkMovementMethod.getInstance());
        link5.setText(Html.fromHtml(
                "<a href='http://www.clipartbest.com/power-point-borders'> Main menu icon link </a>"
        ));

        TextView link6 = (TextView) findViewById(R.id.link6);
        link6.setClickable(true);
        link6.setMovementMethod(LinkMovementMethod.getInstance());
        link6.setText(Html.fromHtml(
                "<a href='https://www.easyicon.net/1186092-bus_icon.html'> Car icons link </a>"
        ));

        TextView link7 = (TextView) findViewById(R.id.link7);
        link7.setClickable(true);
        link7.setMovementMethod(LinkMovementMethod.getInstance());
        link7.setText(Html.fromHtml(
                "<a href='http://www.flaticon.com/'> Other icons can be found at here </a>"
        ));


    }
    private void changeFont(TextView tv) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/bold.ttf");
        tv.setTypeface(typeface);
    }
}
