package com.sagalasan.motorcycleridetracker;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sagalasan.motorcycleridetracker.R;

import java.util.ArrayList;

public class ShowSavedRoute extends ActionBarActivity implements MotorcycleData
{
    TextView editName;
    TextView tripTimeTV;
    TextView tripDistanceTV;
    TextView tripAvgSpeedTV;
    TextView tripAvgSpeedMovTv;
    TextView tripStopTimeTV;
    TextView tripHeadingTV;
    TextView nameTakenTV;
    Button saveRouteButton;

    MyDBHandler dbHandler;

    private ArrayList<MotorcyclePoint> totalRide;

    private String routeName;

    private long begin;
    private long end;
    private long elapsedTime;
    private float totalDistance;
    private float averageSpeed;
    private float averageSpeedMoving;
    private long movingCount;
    private long stopTime;

    private float totalSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_route);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editName = (TextView) findViewById(R.id.route_name);
        tripTimeTV = (TextView) findViewById(R.id.trip_time);
        tripTimeTV = (TextView) findViewById(R.id.trip_time);
        tripDistanceTV = (TextView) findViewById(R.id.distance);
        tripAvgSpeedTV = (TextView) findViewById(R.id.avg_speed);
        tripAvgSpeedMovTv = (TextView) findViewById(R.id.avg_moving_speed);
        tripStopTimeTV = (TextView) findViewById(R.id.stop_time);
        tripHeadingTV = (TextView) findViewById(R.id.heading);

        dbHandler = new MyDBHandler(this, null, null, 1);

        totalRide = new ArrayList<MotorcyclePoint>();

        Intent intent = getIntent();
        routeName = intent.getStringExtra(ROUTE_NAME);

        getRideData();
        initVisual();
    }

    private void initVisual()
    {
        editName.setText(routeName);

        String eTime = returnElapsedTime(0, elapsedTime);
        String sTime = returnElapsedTime(0, stopTime);

        tripTimeTV.setText(eTime);
        tripDistanceTV.setText(String.format("%.1f mi", totalDistance));
        tripAvgSpeedTV.setText(String.format("%.1f mph", averageSpeed));
        tripAvgSpeedMovTv.setText(String.format("%.1f mph", averageSpeedMoving));
        tripStopTimeTV.setText(sTime);
    }

    private void getRideData()
    {
        totalRide = dbHandler.returnRidePoints(routeName);

        double pLat, pLon, lat, lon;

        float[] dist = new float[1];

        totalDistance = 0;
        averageSpeed = 0;
        averageSpeedMoving = 0;
        totalSpeed = 0;

        begin = totalRide.get(0).get_time();
        end = totalRide.get(totalRide.size() - 1).get_time();
        elapsedTime = end - begin;
        movingCount = totalRide.size() - 2;

        /*       fdsafdsafd              */
        totalSpeed += totalRide.get(1).get_speed();
        pLat = totalRide.get(1).get_latitude();
        pLon = totalRide.get(1).get_longitude();

        for(int i = 2; i < totalRide.size() - 1; i++)
        {
            totalSpeed += totalRide.get(i).get_speed();
            lat = totalRide.get(i).get_latitude();
            lon = totalRide.get(i).get_longitude();
            Location.distanceBetween(pLat, pLon, lat, lon, dist);
            totalDistance += dist[0];
            pLat = lat;
            pLon = lon;
        }

        averageSpeedMoving = totalSpeed / movingCount;
        averageSpeed = totalSpeed / elapsedTime * 1000;

        stopTime = elapsedTime - movingCount * 1000;

        totalDistance *= 0.000621371;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_saved_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
