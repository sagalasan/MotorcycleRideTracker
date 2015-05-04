package com.sagalasan.motorcycleridetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.TextView;

import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;


public class Speedometer extends Activity implements LocationListener, MotorcycleData
{
    private int count;

    ImageView needle;
    ImageView gauge;
    Button startTracking;
    TextView countView, splitView;

    private double lastLatVal = 0;
    private double lastLonVal = 0;

    private boolean tracking;
    private Timer timer;
    private SpeedometerView sv;
    final Handler handler = new Handler();
    float someSpeed = 0;

    private float speed;
    private long startTime;
    private float totalDistance;
    private float bearing;

    private double totalSpeed;
    private double totalSpeedMoving;

    private float averageSpeed = 0;
    private float averageSpeedMoving;
    private long movingCount;

    private long theTime;

    private long prevTime = 0;

    private GregorianCalendar sDate, cDate;
    MyDBHandler dbHandler;
    MotorcyclePoint mp;
    MotorcyclePoint mpPrev;

    LocationManager lm;
    Criteria criteria;

    Runnable myRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if(tracking)
            {
                sv.setElapsedTime(String.valueOf(returnElapsedTime(startTime, System.currentTimeMillis())));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        startTracking = (Button) findViewById(R.id.start_tracking);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sv = (SpeedometerView) findViewById(R.id.speedo);
        sv.setScreenSize(size.x, size.y);

        startTime = 0;

        sv.setStartTime(System.currentTimeMillis());

        splitView = (TextView) findViewById(R.id.split_time);


        countView = (TextView) findViewById(R.id.count);
        count = 0;
        countView.setText(String.valueOf(count));

        totalDistance = 0;

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);


        this.onLocationChanged(null);

        mp = new MotorcyclePoint(name);
        mpPrev = new MotorcyclePoint(name);

        prevTime = System.currentTimeMillis();

        totalSpeed = 0;
        totalSpeedMoving = 0;
        averageSpeed = 0;
        averageSpeedMoving = 0;
        movingCount = 0;

        startTracking.setText("Start Tracker");
        tracking = false;
        dbHandler = new MyDBHandler(this, null, null, 1);

        //dbHandler.onUpgrade(null, 1, 1);

        dbHandler.deleteMotorcycleRoute(name);

        dbHandler.addMotorcyclePoint(new MotorcyclePoint("hi"));
        dbHandler.addMotorcyclePoint(new MotorcyclePoint("blah"));



        sv.reset();
        sv.setSpeed(0);
        startup();
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                startup();
            }
        }, 0, 1000);
    }

    public void startTracker(View view)
    {
        if (!tracking)
        {
            tracking = true;
            startTracking.setText("Tracking...");
            startTracking.setTextColor(Color.RED);
            sDate = new GregorianCalendar();
            cDate = new GregorianCalendar();
            startTime = sDate.getTimeInMillis();
            mp = new MotorcyclePoint(name);
            mp.set_time(startTime);
            dbHandler.addMotorcyclePoint(mp);
        }
        else if (tracking)
        {
            long finalTime;
            finalTime = System.currentTimeMillis();
            tracking = false;
            startTracking.setText("Start Tracker");
            mp = new MotorcyclePoint(name);
            mp.set_time(finalTime);
            dbHandler.addMotorcyclePoint(mp);
            startTracking.setTextColor(Color.BLACK);

            Intent intent = new Intent(this, PostTrackingEdit.class);
            intent.putExtra(ELAPSED_TIME, finalTime - startTime);
            intent.putExtra(TOTAL_DISTANCE, totalDistance);
            intent.putExtra(AVERAGE_SPEED, averageSpeed);
            intent.putExtra(AVERAGE_SPEEDM, averageSpeedMoving);
            intent.putExtra(SPEED_COUNT, movingCount);

            startActivity(intent);
        }
    }

    public void viewRoutes(View view)
    {
        tracking = false;
        dbHandler.deleteMotorcycleRoute(name);
        Intent intent = new Intent(this, MotorcycleSavedRoutes.class);

        startActivity(intent);
    }

    private void startup()
    {
        handler.post(myRunnable);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        long s;
        long f;
        long d;
        s = System.currentTimeMillis();
        if(tracking)
        {
            sv.setElapsedTime(String.valueOf(returnElapsedTime(startTime, System.currentTimeMillis())));
        }
        if (location != null)
        {
            speed = location.getSpeed();
            speed *= 2.23694;
            sv.setSpeed(speed);

            bearing = location.getBearing();
            sv.setBearing(String.valueOf(bearing));

            if (tracking)
            {
                long time;
                time = System.currentTimeMillis();
                sv.setElapsedTime(returnElapsedTime(startTime, time));
                //totalSpeed += speed * (time - prevTime) / 1000 / 60 / 60;
                prevTime = time;

                //averageSpeed = (float) totalSpeed / (time - startTime) * 60 * 60 * 1000;

                if(speed >= 2 || count == 0)
                {
                    double latitude;
                    double longitude;
                    long elevation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    elevation = 0;
                    mp = new MotorcyclePoint(name);
                    mp.setGpsPoint(time, latitude, longitude, elevation);
                    mp.set_speed(speed);
                    dbHandler.addMotorcyclePoint(mp);
                    Log.e("adding", "adding");
                    countView.setText(String.valueOf(++count));

                    movingCount++;
                    totalSpeedMoving += speed;
                    averageSpeedMoving = (float) totalSpeedMoving / movingCount;

                    totalSpeed += speed;
                    averageSpeed = (float) totalSpeed / ((System.currentTimeMillis() - startTime) / 1000);

                    sv.setAverageSpeedAll(String.format("%.2f mph", averageSpeed));
                    sv.setAverageSpeedMoving(String.format("%.2f mph", averageSpeedMoving));

                    if(count > 1)
                    {


                        float dist;
                        float[] distArray = new float[1];
                        Location.distanceBetween(lastLonVal, lastLatVal, longitude, latitude, distArray);
                        dist = distArray[0];
                        dist *= 0.000621371;
                        totalDistance += dist;
                        sv.setTotalDistance(String.format("%.1f mi", totalDistance));
                    }
                    lastLatVal = latitude;
                    lastLonVal = longitude;
                    mpPrev = mp;
                }
                else if(speed < 2)
                {

                }
            }
            f = System.currentTimeMillis();
            d = f - s;
            splitView.setText(String.valueOf(d));
        }
        else
        {
            sv.setBearing("---");
            sv.setSpeed(-1);
        }
    }

    private String returnElapsedTime(long sTime, long cTime)
    {
        String result = "";
        long seconds, minutes, hours;

        long time = cTime - sTime;
        seconds = (time / 1000)  % 60;
        minutes = (time / 1000 / 60) % 60;
        hours = (time / 1000 / 60 / 60);

        if(hours < 10)
        {
            result += "0" + hours;
        }
        else
        {
            result += hours;
        }
        result += ":";
        if(minutes < 10)
        {
            result += "0" + minutes;
        }
        else
        {
            result += minutes;
        }
        result += ":";
        if(seconds < 10)
        {
            result += "0" + seconds;
        }
        else
        {
            result += seconds;
        }

        return result;
    }

    private double distBetween(double fLat, double fLon, double iLat, double iLon)
    {
        /*double radius = 3958.75;
        double dLat = Math.toRadians(fLat - iLat);
        double dLon = Math.toRadians(fLon - iLon);
        double xLat = radius * dLat;
        double xLon = radius * dLon;
        double distSquared = (xLat * xLat + xLon * xLon);
        double dist = Math.sqrt(distSquared);*/

        double dist = 0;
        return dist;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
