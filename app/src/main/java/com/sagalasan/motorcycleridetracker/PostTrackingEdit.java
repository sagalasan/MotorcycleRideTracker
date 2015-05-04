package com.sagalasan.motorcycleridetracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class PostTrackingEdit extends ActionBarActivity implements MotorcycleData
{
    EditText editName;
    TextView tripTimeTV;
    TextView tripDistanceTV;
    TextView tripAvgSpeedTV;
    TextView tripAvgSpeedMovTv;
    TextView tripStopTimeTV;
    TextView tripHeadingTV;
    TextView nameTakenTV;
    Button saveRouteButton;

    Long time;

    private long elapsedTime;
    private float totalDistance;
    private float averageSpeed;
    private float averageSpeedMoving;
    private long movingCount;
    private long stopTime;

    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tracking_edit);

        Intent intent = getIntent();
        elapsedTime = intent.getLongExtra(ELAPSED_TIME, 1);
        totalDistance = intent.getFloatExtra(TOTAL_DISTANCE, 1);
        averageSpeed = intent.getFloatExtra(AVERAGE_SPEED, 1);
        averageSpeedMoving = intent.getFloatExtra(AVERAGE_SPEEDM, 1);
        movingCount = intent.getLongExtra(SPEED_COUNT, 1);

        stopTime = elapsedTime - movingCount * 1000;

        dbHandler = new MyDBHandler(this, null, null, 1);
        init();

        editName.addTextChangedListener(new TextValidator(editName)
        {
            @Override
            public void validate(TextView tv, String text)
            {

            }
        });
    }

    private void init()
    {
        editName = (EditText) findViewById(R.id.route_name);
        tripTimeTV = (TextView) findViewById(R.id.trip_time);
        tripDistanceTV = (TextView) findViewById(R.id.distance);
        tripAvgSpeedTV = (TextView) findViewById(R.id.avg_speed);
        tripAvgSpeedMovTv = (TextView) findViewById(R.id.avg_moving_speed);
        tripStopTimeTV = (TextView) findViewById(R.id.stop_time);
        tripHeadingTV = (TextView) findViewById(R.id.heading);
        nameTakenTV = (TextView) findViewById(R.id.name_taken);
        saveRouteButton = (Button) findViewById(R.id.save_route);

        String eTime = returnElapsedTime(0, elapsedTime);
        String sTime = returnElapsedTime(0, stopTime);

        tripTimeTV.setText(eTime);
        tripDistanceTV.setText(String.format("%.1f mi", totalDistance));
        tripAvgSpeedTV.setText(String.format("%.1f mph", averageSpeed));
        tripAvgSpeedMovTv.setText(String.format("%.1f mph", averageSpeedMoving));
        tripStopTimeTV.setText(sTime);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_tracking_edit, menu);
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

    public abstract class TextValidator implements TextWatcher
    {
        private final TextView tv;

        public TextValidator(TextView tv)
        {
            this.tv = tv;
        }

        public abstract void validate(TextView tv, String text);

        @Override
        final public void afterTextChanged(Editable s)
        {
            String text = tv.getText().toString();
            validate(tv, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

    }

    public void saveRoute(View view)
    {
        String saveName = editName.getText().toString();
        dbHandler.updateName("default", saveName);
        Intent intent = new Intent(this, Speedometer.class);
        startActivity(intent);
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
