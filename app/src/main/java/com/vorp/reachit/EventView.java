package com.vorp.reachit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Himanshu on 2018-06-22.
 */

public class EventView extends ConstraintLayout {
    private String eventName="test";
    private LatLng location;
    private String emoji;
    private Paint paint = new Paint();
    public EventView(Context context, String name, LatLng latlng, String emo) {
        super(context);
        setWillNotDraw(false);
        eventName = name;
        location=latlng;
        emoji=emo;
        Log.d("event test",emoji);
    }

    public EventView(Context context,Event event)
    {
        this(context,event.getName(),event.getLocation(),event.getEmoji());
    }
    public EventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setWillNotDraw(false);
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
    }


    /*
        TODO: CHANGE FROM PIXELS TO DP
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(getResources().getColor(R.color.lightGrey));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setAntiAlias(true);
        canvas.drawText(eventName,canvas.getWidth()/6, canvas.getHeight()/2,paint);
        Log.d("canvasstuff",canvas.getHeight()+" "+ canvas.getWidth());

    }

    public String getEventName()
    {
        return eventName;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public LatLng getLocation() {
        return location;
    }
}
