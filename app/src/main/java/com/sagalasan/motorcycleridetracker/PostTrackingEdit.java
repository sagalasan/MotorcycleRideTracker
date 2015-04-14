package com.sagalasan.motorcycleridetracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class PostTrackingEdit extends ActionBarActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tracking_edit);
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
}
