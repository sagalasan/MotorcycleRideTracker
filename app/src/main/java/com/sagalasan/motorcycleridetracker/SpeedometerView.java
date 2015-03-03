package com.sagalasan.motorcycleridetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;

/**
 * Created by Christiaan on 2/26/2015.
 */
public class SpeedometerView extends View
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

    /**
     * Initializes the all the parts of the GUI in the custom view
     */
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
        setMeasuredDimension(mWidth, newDimen);
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
        tempBitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
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
