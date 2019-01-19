package com.elchananalon.decibelmeter;

public class Measurement {
    private double db;
    private String curr_time;
    private String place;
    private String waypoints;


    public Measurement(double db, String place, String waypoints, String curr_t){
        this.curr_time = curr_t;
        this.waypoints = waypoints;
        this.place = place;
        this.db = db;
    }
    //*****getters and setter******************//
    // Each measurement CANNOT be changed after it's done - it just won't be right (the measurement is done). so no Set methods
    public double getDb() {
        return db;
    }

    public String getCurr_time() {
        return curr_time;
    }

    public String getPlace() { return place; }

    public String getWaypoints() { return waypoints; }

}
