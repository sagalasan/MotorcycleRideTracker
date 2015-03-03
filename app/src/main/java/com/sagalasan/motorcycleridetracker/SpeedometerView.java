package com.sagalasan.motorcycleridetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;

import java.util.Timer;

/**
 * Created by Christiaan on 2/26/2015.
 */
public class SpeedometerView extends View implements SensorEventListener
{
    private int screenWidth, screenHeight;
    private int cx, cy;
    private int width, height, newDimen, mWidth, mHeight;
    private float scale;

    private Canvas tempCanvas;
    private Bitmap bitGauge;
    private Bitmap bitNeedle;
    private Bitmap tempBitmap;

    private Matrix matrixNeedle;
    private Matrix matrixGauge;
    private RotateAnimation aStart;

    private float angle;

    private Paint paint;

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


    private static final String TAG = "MyActivity";

    public SpeedometerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        bitNeedle = BitmapFactory.decodeResource(getResources(), R.drawable.speedometer_needle_blurred);
        bitGauge = BitmapFactory.decodeResource(getResources(), R.drawable.speedometer_face_blurred);

        paint = new Paint();

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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        //Log.v(TAG, String.valueOf(mWidth));
        setMeasuredDimension(mWidth, newDimen);
    }

    private float degreeToAngle(float degrees)
    {
        return (degrees + zeroDegrees);
    }

    private void reDraw()
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        this.invalidate();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawGauge();
        drawNeedle();



        //tempCanvas.drawBitmap(bitGauge, matrixGauge, paint);
        //tempCanvas.drawBitmap(bitNeedle, matrixNeedle, paint);

        cx = (mWidth - newDimen) / 2;
        cy = (mWidth - newDimen) / 2;

        canvas.drawBitmap(tempBitmap, cx, cy, paint);

        if(needleNeedsToMove())
        {
            moveNeedle();
        }
    }

    private void drawGauge()
    {
        tempCanvas.drawBitmap(bitGauge, matrixGauge, paint);
    }

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

    private void moveNeedle()
    {
        if(!needleNeedsToMove()) return;

        if(lastNeedleMoveTime != -1L)
        {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastNeedleMoveTime) / 1000.0f;

            float direction = Math.signum(needleVelocity);
            //double exponent = (double) (Math.abs((needleTarget - needlePosition) * .3));
            //needleVelocity = (float) (5.0f * Math.pow(2, (exponent - 2) * delta * direction));
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

    private boolean needleNeedsToMove()
    {
        return Math.abs(needleTarget - needlePosition) > .01f;
    }

    public void setSpeed(float speed)
    {
        needleTarget = (speed * degreesPerTick) / 10;
        reDraw();
    }

    public void reset()
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        matrixNeedle.postRotate(0, newDimen / 2, newDimen / 2);
        this.invalidate();
    }

    public void update(float a)
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        matrixNeedle.postRotate(a, newDimen / 2, newDimen / 2);
        angle = a;
        if(angle >= 360) angle = 0;
        this.invalidate();
    }

    public void startup()
    {
        tempBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);

    }

    public void setScreenSize(int x, int y)
    {
        screenWidth = x;
        screenHeight = y;
    }

    public float returnNeedleTarget()
    {
        return needleTarget;
    }
    public float returnNeedleVelocity()
    {
        return needleVelocity;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event)
    {

    }
}
