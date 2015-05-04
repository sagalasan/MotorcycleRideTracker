package com.sagalasan.motorcycleridetracker;

import android.content.Intent;
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

    MyDBHandler dbHandler;s

    private ArrayList<MotorcyclePoint> totalRide;

    private String routeName;

    private long elapsedTime;
    private float totalDistance;
    private float averageSpeed;
    private float averageSpeedMoving;
    private long movingCount;
    private long stopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_route);
        editName = (TextView) findViewById(R.id.route_name);
        tripTimeTV = (TextView) findViewById(R.id.trip_time);

        dbHandler = new MyDBHandler(this, null, null, 1);

        totalRide = new ArrayList<MotorcyclePoint>();

        Intent intent = getIntent();
        routeName = intent.getStringExtra(ROUTE_NAME);

        getRideData();
    }

    private void initVisual()
    {
        editName.setText(name);
    }

    private void getRideData()
    {
        totalRide =
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
}
