package com.elchananalon.decibelmeter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.sql.Timestamp;


public class Locator implements LocationListener {


    private LocationManager locationManager;
    private boolean isTrackLocation;
    Context mContext;


    public Locator(Context mContext) {
        this.mContext = mContext;
        this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

    }

    public String trackLocation()
    {
        String place = "Location not found";
        if (!isTrackLocation)
        {
            //TODO Check for permission and remove try&catch

            try {
                place = getPlace(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                //   double longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                //  double latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                // start track GPS location as soon as possible or location changed
                //long minTime = 0;       // minimum time interval between location updates, in milliseconds
               // float minDistance = 0;  // minimum distance between location updates, in meters
              //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
                isTrackLocation = true;
            } catch (SecurityException e) {
                Log.d("Debug", "ERROR");
            }


        }
        //locationManager.removeUpdates(this);

        return place;
    }

    public String getPlace(Location location){
        String place="Not Found";
        System.out.println(location);
        if (location != null) {
            place = "Latitude: " + Double.toString(location.getLatitude()) + " | Longtitude: " + Double.toString(location.getLongitude()) + " | Time: " + new Timestamp(location.getTime());
        }


        return place;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
    }
    @Override
    public void onProviderEnabled(String s)
    {
    }

    @Override
    public void onProviderDisabled(String s)
    {
    }

    public void stopTracking(){
        locationManager.removeUpdates(this);
        isTrackLocation = false;
    }


}
