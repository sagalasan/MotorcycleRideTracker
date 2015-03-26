package com.sagalasan.motorcycleridetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

/**
 * Created by Christiaan on 2/26/2015.
 */
public class SpeedometerView extends View
{
    private int screenWidth, screenHeight;
    private int cx, cy;
    private int width, height, newDimen, mWidth, mHeight;
    private float scale;

    private boolean gpsStatus = false;

    private Canvas tempCanvas;
    private Bitmap bitGauge;
    private Bitmap bitNeedle;
    private Bitmap tempBitmap;
    private Bitmap bitSpeedo;

    private Matrix matrixNeedle;
    private Matrix matrixGauge;
    private RotateAnimation aStart;

    private float angle;
    private float speed;

    private Paint paint;
    private Paint speedoPaint;
    private Paint speedoUnitPaint;
    private Paint nameTextPaint;
    private Paint infoTextPaint;
    private Paint subTextPaint;

    private String speedUnits;

    private static final float degreesPerTick = 18.0f;
    private static final float minDegrees = -36.0f;
    private static final float maxDegrees = 216.0f;
    private static final float zeroDegrees = minDegrees;

    private boolean needleInitialized = true;
    private float needleTarget = 0.0f;
    private float needlePosition = 0.0f;
    private float needlePositionPrevious = 0.0f;
    private float needleVelocity = 0.0f;
    private float needleAcceleration = 0.0f;
    private long lastNeedleMoveTime = -1L;

    private String trip = "Trip";
    private String averageSpeed = "Avg Speed";
    private String stopTime = "Stopped Time";
    private String leanAngle = "Lean Angle";
    private String heading = "Heading";
    private static final int boxZero = 1025;

    private static final float speedoTextSize = 200.0f;
    private static final float nameTextSize = 100.0f;
    private static final float infoTextSize = 100.0f;
    private static final float subTextSize = 35.0f;
    private static final float textMargin = 30.0f;

    private long startTime;
    private long tripTime;
    private String elapsedTime = "00:00:00";

    private static final String TAG = "MyActivity";

    public SpeedometerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Initializes the all the parts of the GUI in the custom view
     */
    private void init()
    {
        bitNeedle = BitmapFactory.decodeResource(getResources(), R.drawable.speedometer_needle_blurred);
        bitGauge = BitmapFactory.decodeResource(getResources(), R.drawable.speedometer_face_blurred);

        speedUnits = "MPH";

        initializePaints();

        width = bitNeedle.getWidth();
        height = bitNeedle.getHeight();

        newDimen = 1080;

        scale = ((float) newDimen) / width;

        matrixNeedle = new Matrix();
        matrixGauge = new Matrix();

        matrixNeedle.postScale(scale, scale);
        matrixGauge.postScale(scale, scale);
        matrixNeedle.postRotate(0, width / 2, height / 2);

        tempBitmap = Bitmap.createBitmap(newDimen, newDimen, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    private void initializePaints()
    {
        paint = new Paint();

        speedoPaint = new Paint();
        speedoPaint.setColor(Color.WHITE);
        speedoPaint.setTextSize(speedoTextSize);
        speedoPaint.setTextAlign(Paint.Align.CENTER);

        speedoUnitPaint = new Paint();
        speedoUnitPaint.setColor(Color.WHITE);
        speedoUnitPaint.setTextSize(speedoTextSize - 150);
        speedoUnitPaint.setTextAlign(Paint.Align.CENTER);

        nameTextPaint = new Paint();
        nameTextPaint.setColor(Color.WHITE);
        nameTextPaint.setTextAlign(Paint.Align.CENTER);
        nameTextPaint.setTextSize((int) nameTextSize);

        infoTextPaint = new Paint();
        infoTextPaint.setColor(Color.WHITE);
        infoTextPaint.setTextAlign(Paint.Align.CENTER);
        infoTextPaint.setTextSize((int) infoTextSize);

        subTextPaint = new Paint();
        subTextPaint.setColor(Color.WHITE);
        subTextPaint.setTextAlign(Paint.Align.LEFT);
        subTextPaint.setTextSize(subTextSize);
    }

    /**
     * Override method onMeasure
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        //Log.v(TAG, String.valueOf(mWidth));
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * Calibrate angle to the zero angle of the speedometer
     * @param degrees
     * @return
     */
    private float degreeToAngle(float degrees)
    {
        return (degrees + zeroDegrees);
    }

    /**
     * Function that is called so that the view can be invalidated
     */
    private void reDraw()
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        this.invalidate();
    }

    /**
     * Override onDraw method for custom view
     * @param canvas
     */
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawGauge();
        drawNeedle();

        cx = (mWidth - newDimen) / 2;
        cy = (mWidth - newDimen) / 2;

        canvas.drawBitmap(tempBitmap, cx, cy, paint);

        drawDigitalSpeedo(canvas);
        drawInfoBoxTrip(canvas);
        drawInfoBoxAvgSpeed(canvas);

        if(needleNeedsToMove())
        {
            moveNeedle();
        }
    }

    /**
     * Helper method that draws the gauge to a canvas
     */
    private void drawGauge()
    {
        tempCanvas.drawBitmap(bitGauge, matrixGauge, paint);
    }

    /**
     * Helper method that draws the needle to a canvas
     */
    private void drawNeedle()
    {
        if(needleInitialized)
        {
            float handAngle = degreeToAngle(needlePosition);
            //matrixNeedle = matrixGauge;
            matrixNeedle = new Matrix();
            matrixNeedle.postScale(scale, scale);
            matrixNeedle.postRotate(handAngle, newDimen / 2, newDimen / 2);
            tempCanvas.drawBitmap(bitNeedle, matrixNeedle, paint);
        }
    }

    private void drawDigitalSpeedo(Canvas canvas)
    {
        int currentSpeed = (int) speed;
        String speedText = String.valueOf(currentSpeed);
        canvas.drawText(speedText, mWidth / 2, mWidth - speedoTextSize - 70, speedoPaint);
        canvas.drawText(speedUnits, mWidth / 2, mWidth - speedoTextSize - 10, speedoUnitPaint);
    }

    private void drawInfoBoxTrip(Canvas canvas)
    {
        canvas.drawText(trip, mWidth / 4, boxZero, nameTextPaint);
        canvas.drawText("00.00 mi", mWidth / 4, boxZero + infoTextSize + textMargin, infoTextPaint);
        canvas.drawText(elapsedTime, mWidth / 4, boxZero + 2 * (infoTextSize + textMargin), infoTextPaint);
    }

    private void drawInfoBoxAvgSpeed(Canvas canvas)
    {
        canvas.drawText(averageSpeed, 3 * mWidth / 4, boxZero, nameTextPaint);
        canvas.drawText("Moving:", mWidth / 2 + 30, boxZero + nameTextSize / 2 - 10, subTextPaint);
        canvas.drawText("00.0 MPH", 3 * mWidth / 4, boxZero + nameTextSize + textMargin, infoTextPaint);
        canvas.drawText("All:", mWidth / 2 + 30, boxZero + nameTextSize + 2 * textMargin + 10, subTextPaint);
        canvas.drawText("00.0 MPH", 3 * mWidth / 4, boxZero + 2 * (infoTextSize + textMargin), infoTextPaint);
    }

    /**
     * Main method for calculating the movement of the needle
     */
    private void moveNeedle()
    {
        if(!needleNeedsToMove()) return;

        if(lastNeedleMoveTime != -1L)
        {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastNeedleMoveTime) / 1000.0f;

            float direction = Math.signum(needleVelocity);
            if (Math.abs(needleVelocity) < 90.0f) {
                needleAcceleration = 1.0f * (needleTarget - needlePosition);
            } else {
                needleAcceleration = 0.0f;
            }
            needlePositionPrevious = needlePosition;
            needlePosition += needleVelocity * delta;
            needleVelocity += needleAcceleration * delta;
            Log.w(TAG, String.valueOf(needlePosition));
            if((needleTarget - needlePosition) * direction < 0.01f * direction)
            {
                needlePosition = needleTarget;
                needleVelocity = 0.0f;
                needleAcceleration = 0.0f;
                lastNeedleMoveTime = -1L;
            }
            else
            {
                lastNeedleMoveTime = System.currentTimeMillis();
            }
            reDraw();
        }
        else
        {
            lastNeedleMoveTime = System.currentTimeMillis();
            moveNeedle();
        }
    }

    /**
     * Determines whether the needle needs to move or not
     * based on the target position of the needle
     * @return
     */
    private boolean needleNeedsToMove()
    {
        return Math.abs(needleTarget - needlePosition) > .01f;
    }

    /**
     * Method that can be called publicly that will set the current speed of the speedometer
     * @param speed
     */
    public void setSpeed(float speed)
    {
        needleTarget = (speed * degreesPerTick) / 10;
        this.speed = speed;
        reDraw();
    }

    /**
     * Public method that will reset the needle to 0;
     */
    public void reset()
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        matrixNeedle.postRotate(0, newDimen / 2, newDimen / 2);
        this.invalidate();
    }

    /**
     * Method imports the screen size
     * Called publicly
     * @param x
     * @param y
     */
    public void setScreenSize(int x, int y)
    {
        screenWidth = x;
        screenHeight = y;
    }

    public void setTripTime(long s)
    {
        tripTime = s;
        long elapsed = tripTime - startTime;
        long secs = elapsed / 1000;
        long mins = elapsed / 1000 / 60;
        long hours = elapsed / 1000 /60 / 60;
        secs = secs % 60;
        mins = mins % 60;
        elapsedTime = hours + ":" + mins + ":" + secs;
    }

    public void setStartTime(long s)
    {
        startTime = s;
    }

    public void setGpsStatus(boolean b)
    {
        gpsStatus = b;
    }

    /**
     * Returns the target angle of the needle
     * @return
     */
    public float returnNeedleTarget()
    {
        return needleTarget;
    }

    /**
     * Returns the the current needle velocity
     * @return
     */
    public float returnNeedleVelocity()
    {
        return needleVelocity;
    }
}
