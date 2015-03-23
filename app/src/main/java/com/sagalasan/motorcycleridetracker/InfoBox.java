package com.sagalasan.motorcycleridetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Christiaan on 3/22/2015.
 */
public class InfoBox extends View
{
    private String name = "Default";

    private Paint paint;

    private Bitmap bitBox;

    public InfoBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        bitBox = BitmapFactory.decodeResource(getResources(), R.drawable.info_box);

        paint = new Paint();
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(bitBox, 0, 0, paint);
    }

    public void setName(String n)
    {
        name = n;
    }
}
