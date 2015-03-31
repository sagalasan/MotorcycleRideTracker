package com.sagalasan.motorcycleridetracker;

/**
 * Created by Christiaan on 3/31/2015.
 */
public class MotorcyclePoint
{
    private int _id;
    private String _name;
    private long _time;
    private String _latitude;
    private String _longitude;
    private long _elevation;
    private double _speed;
    private double _lean;

    public MotorcyclePoint()
    {
    }

    public MotorcyclePoint(String _name, long _time, String _latitude, String _longitude, long _elevation, double _speed, double _lean)
    {
        this._name = _name;
        this._time = _time;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._elevation = _elevation;
        this._speed = _speed;
        this._lean = _lean;
    }

    public void setGpsPoint(long _time, String _latitude, String _longitude, long _elevation)
    {
        this._time = _time;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._elevation = _elevation;
    }

    public void set_speed(double _speed)
    {
        this._speed = _speed;
    }

    public void set_lean(double _lean)
    {
        this._lean = _lean;
    }

    public String get_name() {
        return _name;
    }

    public long get_time() {
        return _time;
    }

    public String get_latitude() {
        return _latitude;
    }

    public String get_longitude() {
        return _longitude;
    }

    public long get_elevation() {
        return _elevation;
    }

    public double get_speed() {
        return _speed;
    }

    public double get_lean() {
        return _lean;
    }
}
