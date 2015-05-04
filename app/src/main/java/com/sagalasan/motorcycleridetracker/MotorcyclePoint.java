package com.sagalasan.motorcycleridetracker;

/**
 * Created by Christiaan on 3/31/2015.
 */
public class MotorcyclePoint
{
    private int _id;
    private String _name;
    private long _time;
    private double _latitude;
    private double _longitude;
    private long _elevation;
    private double _speed;
    private double _lean;

    public MotorcyclePoint()
    {
    }

    public MotorcyclePoint(String _name)
    {
        this._name = _name;
    }

    public MotorcyclePoint(String _name, long _time, double _latitude, double _longitude, long _elevation, double _speed, double _lean)
    {
        this._name = _name;
        this._time = _time;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._elevation = _elevation;
        this._speed = _speed;
        this._lean = _lean;
    }

    public MotorcyclePoint(String _name, long _time, double _latitude, double _longitude, double _speed)
    {
        this._name = _name;
        this._time = _time;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._speed = _speed;
    }

    public void setGpsPoint(long _time, double _latitude, double _longitude, long _elevation)
    {
        this._time = _time;
        this._latitude = _latitude;
        this._longitude = _longitude;
        this._elevation = _elevation;
        _lean = 0;
    }

    public void set_speed(double _speed)
    {
        this._speed = _speed;
    }

    public void set_lean(double _lean)
    {
        this._lean = _lean;
    }

    public void set_time(long time) { this._time = time;}

    public String get_name() {
        return _name;
    }

    public long get_time() {
        return _time;
    }

    public double get_latitude() {
        return _latitude;
    }

    public double get_longitude() {
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
