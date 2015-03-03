package com.sagalasan.motorcycleridetracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;


public class Speedometer extends ActionBarActivity {

    ImageView needle;
    ImageView gauge;
    private Timer timer;
    private SpeedometerView sv;
    final Handler handler = new Handler();
    TextView ag;
    float someSpeed = 0;

    Runnable myRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            ag.setText(String.valueOf(sv.returnNeedleTarget()) + " " + String.valueOf(sv.returnNeedleVelocity()));
            someSpeed += 2;
            //sv.setSpeed(someSpeed);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sv = (SpeedometerView) findViewById(R.id.speedo);
        sv.setScreenSize(size.x, size.y);

        ag = (TextView) findViewById(R.id.currentAngle);


        sv.reset();
        sv.setSpeed(0);
        //startup();
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                startup();
            }
        }, 0, 100);
    }

    private void startup()
    {
        handler.post(myRunnable);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speedometer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
