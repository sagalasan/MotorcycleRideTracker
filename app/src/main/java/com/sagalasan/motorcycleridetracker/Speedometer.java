package com.sagalasan.motorcycleridetracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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


public class Speedometer extends Activity implements LocationListener {

    ImageView needle;
    ImageView gauge;
    private Timer timer;
    private SpeedometerView sv;
    final Handler handler = new Handler();
    float someSpeed = 0;
    private float speed;

    Runnable myRunnable = new Runnable()
    {
        @Override
        public void run()
        {

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

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 250, 0, this);

        this.onLocationChanged(null);

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
        }, 0, 250);
    }

    private void startup()
    {
        handler.post(myRunnable);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        speed = location.getSpeed();
        sv.setSpeed(speed);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
