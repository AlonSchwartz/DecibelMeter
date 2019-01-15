package com.elchananalon.decibelmeter;


import android.util.Log;

import java.util.Date;


public class Measurement {
    private double db;
    private double latitude;
    private double longitude;
    private Date curr_time;


    public Measurement(double db,double lat, double lon, Date curr_t){
        this.curr_time = curr_t;
        this.latitude = lat;
        this.longitude = lon;
        this.db = db;
    }
    //*****getters and setter******************//
    public double getDb() {
        return db;
    }
    public void setDb(double db) {
        this.db = db;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getCurr_time() {
        return curr_time;
    }

    public void setCurr_time(Date curr_time) {
        this.curr_time = curr_time;
    }
}
