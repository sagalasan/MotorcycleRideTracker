package com.sagalasan.motorcycleridetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Christiaan on 3/31/2015.
 */

public class MyDBHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tracker.db";
    public static final String TABLE_MOTORCYCLE = "trackertable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "ridename";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ELEVATION = "elevation";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_LEAN = "lean";

    ArrayList<String> routes;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE " + TABLE_MOTORCYCLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME + " INTEGER, " +
                COLUMN_LATITUDE + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_ELEVATION + " INTEGER, " +
                COLUMN_SPEED + " INTEGER, " +
                COLUMN_LEAN + " INTEGER" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db = getWritableDatabase();
        String query = "DROP TABLE IF EXISTS " + TABLE_MOTORCYCLE;
        db.execSQL(query);
        onCreate(db);
    }

    public void addMotorcyclePoint(MotorcyclePoint mp)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, mp.get_name());
        values.put(COLUMN_TIME, mp.get_time());
        values.put(COLUMN_LATITUDE, String.valueOf(mp.get_latitude()));
        values.put(COLUMN_LONGITUDE, String.valueOf(mp.get_longitude()));
        values.put(COLUMN_ELEVATION, mp.get_elevation());
        values.put(COLUMN_SPEED, mp.get_speed());
        values.put(COLUMN_LEAN, mp.get_lean());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MOTORCYCLE, null, values);
        db.close();
    }

    public void deleteMotorcycleRoute(String name)
    {
        SQLiteDatabase db = getWritableDatabase();
        String mpName = name;
        String query = "DELETE FROM " + TABLE_MOTORCYCLE + " WHERE " + COLUMN_NAME + "=\"" + mpName + "\";";
        db.execSQL(query);
        db.close();
    }

    public ArrayList<String> returnRoutes()
    {
        routes = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOTORCYCLE + " WHERE 1";
        String currentName = "";
        String tempName = "";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        Log.e("dummY", "dummmmy");

        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex(COLUMN_NAME)) != null)
            {
                tempName = c.getString(c.getColumnIndex(COLUMN_NAME));
                if(!doesStringExist(tempName))
                {
                    Log.e("hi", "hi");
                    routes.add(tempName);
                }
                currentName = tempName;

                c.moveToNext();
            }
        }
        c.close();
        db.close();

        return routes;
    }

    private boolean doesStringExist(String x)
    {
        boolean result = false;
        for(int i = 0; i < routes.size(); i++)
        {
            if(x.equals(routes.get(i)))
            {
                result = true;
            }
        }
        return result;
    }

    public void updateName(String oldName, String newName)
    {
        SQLiteDatabase db = getWritableDatabase();
        String ex = "UPDATE " + TABLE_MOTORCYCLE + " SET " + COLUMN_NAME + "='" + newName + "'  WHERE " + COLUMN_NAME + "='" + oldName + "'";
        db.execSQL(ex);
        db.close();
    }
}
