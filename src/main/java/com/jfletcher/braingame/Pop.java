package com.jfletcher.braingame;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jondf on 12/13/2016.
 */
public class Pop extends Activity {

    //record scoring
    TextView ScoreTextView;
    String ScoreText;
    TextView PointsText;
    String value;
    int points;
    int total;
    Bundle extras = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;


        extras = getIntent().getExtras();
        if (extras != null) {
            points = extras.getInt("points");
            total = extras.getInt("total");
            value = extras.getString("key");
            //The key argument here must match that used in the other activity
        }

        Log.i("Pull", value);
        Log.i("Points", Integer.toString(points));
        Log.i("Tries", Integer.toString(total));


        TextView scoreTextView = (TextView) findViewById(R.id.totalProblemsPop);
        scoreTextView.setText("Out of " + total + " problems!");
        TextView pointsText = (TextView) findViewById(R.id.pointPop);
        pointsText.setText("You scored " + points +" points!");


        getWindow().setLayout((int) (width*0.75),(int) (height*0.75));

    }
}
