package com.elchananalon.decibelmeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Locator implements LocationListener {


    private LocationManager locationManager;
    private boolean isTrackLocation;
    Context mContext;
    private String[] results = new String[2];
    double longitude;
    double latitude;
    String address = "";
    String place = "Location not found";

    public Locator(Context mContext) {
        this.mContext = mContext;
        this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

    }


    // To start tracking location
    public void trackLocation() {
        double longitude;
        double latitude;
        String address = "";
        String place = "Location not found";

       // Criteria criteria = new Criteria();
       // criteria.setAccuracy(Criteria.ACCURACY_FINE);
       // criteria.setAltitudeRequired(false);
       // criteria.setBearingRequired(false);
       // criteria.setCostAllowed(true);
       // criteria.setPowerRequirement(Criteria.POWER_HIGH);

        // String best = locationManager.getBestProvider(criteria,false);
        //Log.d("LOCATOR;", "======================= "+best);

        // Check if user granted permissions to use GPS
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            long minTime = 0;       // minimum time interval between location updates, in milliseconds
            float minDistance = 50;  // minimum distance between location updates, in meters

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
            place = getPlace(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            if (place.equals("Location not found"))
            {
                System.out.println("GPS Location not found, searching network...");
                place = getPlace(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
            }
        }

       address = getAddress(place);

        // saving address + waypoints at the results array
        if (!address.equals("Location not found")){
            results[0] = address;
            results[1] = place;
        }

        else{
            results[0] = place;
            results[1] = "0,0";

        }

    }

    // To get longitude and latitude
    public String getPlace(Location location){
        String place="Location not found";
        System.out.println(location);
        if (location != null) {
            place = Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude());
        }

        return place;
    }

    public boolean isGpsEnabled(){

        mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void stopGPS(){
        locationManager.removeUpdates(this);

    }

    public String[] getResults(){
        return results;
    }

    // gets waypoints and convert to an address
    private String getAddress(String waypoints){

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            List<Address> addresses = null;

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            if (!waypoints.equals("Location not found")) {
                try {
                    longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                    latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (Exception exception) {


                    try{
                        longitude = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude();
                        latitude = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude();
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    }catch (Exception exce2)
                    {
                        Log.d("Locator", "Location not available");

                    }
                    Log.d("Locator", "Location not available");
                }


                if (addresses == null || addresses.size() == 0) {

                    Toast.makeText(mContext, "No address found! saving waypoints", Toast.LENGTH_SHORT).show();
                    address = waypoints;
                } else {
                    address = addresses.get(0).getAddressLine(0);
                }
                return address;
            }
        }
        return waypoints;
    }


    // If location changed, change the results as well
    public void onLocationChanged(Location location)
    {
        String newPlace = getPlace(location);
        String newAddress = getAddress(newPlace);
        Log.d("Current place", ""+newAddress);
        if (!newAddress.equals("Location not found")){
            results[0] = newAddress;
            results[1] = newPlace;
        }

        else{
            results[0] = newPlace;
            results[1] = "0,0";
        }

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
