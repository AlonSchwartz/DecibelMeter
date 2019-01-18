package com.elchananalon.decibelmeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;


public class Locator implements LocationListener {


    private LocationManager locationManager;
    private boolean isTrackLocation;
    Context mContext;


    public Locator(Context mContext) {
        this.mContext = mContext;
        this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

    }


    public String[] trackLocation()
    {
        double longitude;
        double latitude;
        String address = "";

        String place = "Location not found";

        if ( ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            long minTime = 0;       // minimum time interval between location updates, in milliseconds
            float minDistance = 0;  // minimum distance between location updates, in meters
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
            place = getPlace(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            // locationManager.requestSingleUpdate(locationManager.GPS_PROVIDER, this, );

        }
/*
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
*/


        Log.d("Debug", "PLACE IS ---------------------> " + place);
        List<Address> addresses = null;

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            addresses = geocoder.getFromLocation(latitude, longitude , 1);
        }catch(IOException ioException )
        {
            Log.e("Locator", "GPS Service not available");

        }
        Log.d("Locator", "SIZE IS = "+addresses.size());

        if (addresses == null || addresses.size()  == 0) {

            Toast.makeText(mContext, "No address found! saving waypoints", Toast.LENGTH_SHORT).show();
            address = place;
        }else
        {
            address = addresses.get(0).getAddressLine(0);


        }
        Log.d("Locator",address);
        locationManager.removeUpdates(this);
        String[] results = {address, place};
        return results;
    }

    // To get longitude and latitude
    public String getPlace(Location location){
        String place="Not Found";
        System.out.println(location);
        if (location != null) {
            place = Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude());
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

}
