package com.elchananalon.decibelmeter;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DecibelMeasurement extends AppCompatActivity implements View.OnClickListener{

    private Button buttonStart;
    private TextView resultsDb;
    private TextView resultsTime;
    private TextView resultsPlace;
    private Locator loc;
    private TextView resultsLive;
    private ProgressBar pb;
    //private Locator loc;
    private Measurement measurement;
    private String place;
    private String[] locResults;
    private String waypoints;

    private SQLiteDatabase measurementsDB = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};

    boolean isMeasuring = false;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToTrackAccepted = false;
    private boolean firstStart = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_decibel_measurement);
        //getting buttons from xml
        buttonStart = findViewById(R.id.buttonStart);
        resultsDb = findViewById(R.id.txt_db_res);
        resultsLive = findViewById(R.id.txt_live_res);
        resultsPlace = findViewById(R.id.txt_loc_res);
        resultsTime = findViewById(R.id.txt_time_res);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        //attaching on click listeners to buttons
        buttonStart.setOnClickListener(this);

        loc  = new Locator(this);

        // If gps is disables, ask the user to enable it.
        if (!loc.isGpsEnabled()) {
            askToEnableGPS();
        }

        // only for API greater than 23, because requesting for permissions is mandatory since then.
        else if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Ask for permissions to use microphone & gps if does not have permissions
            ActivityCompat.requestPermissions(this, permissions, 0);
        }

        // Init Database
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE, waypoints VARCHAR);";
        measurementsDB.execSQL(sql);




    }
    // Check if the user has granted permission. If not - go back to main page.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToTrackAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!(permissionToRecordAccepted && permissionToTrackAccepted)) {
            measurementsDB.close();
            finish();
        }
    }

    public void onClick(View v)
    {

        //Locator loc  = new Locator(this);
        Log.d("Thread","=====================My ID is: "+android.os.Process.getThreadPriority(android.os.Process.myTid()));


        switch(v.getId()){

            case R.id.buttonStart:
                Log.d("Thread","=====================My ID is: "+android.os.Process.getThreadPriority(android.os.Process.myTid()));

                startService(new Intent(this, MeasurementService.class));

                if (!isMeasuring) {
                    /**
                    * Register to receive messages.
                    * We are registering an observer (mMessageReceiver) to receive Intents
                    */
                    // Get measure details from service
                    if(firstStart) {
                        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
                        firstStart =false;
                    }
                    // Get live measurements
                    LocalBroadcastManager.getInstance(this).registerReceiver(mLiveMessageReceiver, new IntentFilter("live-event-name"));
                    Log.d("Not measuring", "Stopped");
                    buttonStart.setText("Stop");
                    loc.trackLocation();
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


                } else {
                    buttonStart.setText("Start");
                    stopService(new Intent(this, MeasurementService.class));
                    loc.stopGPS();
                    locResults = loc.getResults();
                    place = locResults[0];
                    waypoints = locResults[1];

                }
                isMeasuring = !isMeasuring;
                break;
        }


    }
    private double maxDb = 0.0;
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasts.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double res = intent.getDoubleExtra("measurement_results",0.0);
            Log.d("receiver", "Got message: " +res);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
            Date d = new Date(System.currentTimeMillis());
            String time = dateFormat.format(d);

            //String time = new Timestamp(System.currentTimeMillis()).toString();

            //String time = String.format("%1$tr", System.currentTimeMillis());

            String e = (place.replaceAll("-", " ").replaceAll("'", "")); //Delete - and ' from strings, they are doing errors when uploading to SQLite
            Log.d("Change:", "" + e);
            measurement = new Measurement(res, e, waypoints, time);
            String update = "INSERT INTO measurements (location, timeTaken, result, waypoints) VALUES ('" + measurement.getPlace() + "', '" + measurement.getCurr_time() + "', '" + measurement.getDb() + "', '" + measurement.getWaypoints() + "');";
            measurementsDB.execSQL(update);
            //results.setText("Results: " + measurement.getDb() + " db\nTime: \n" + measurement.getCurr_time() + " \nPlace: \n" + measurement.getPlace());
            resultsDb.setText(String.valueOf(measurement.getDb()));
            resultsTime.setText(String.valueOf(measurement.getCurr_time()));
            resultsPlace.setText(String.valueOf(measurement.getPlace()));
            maxDb = 0.0;
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);


        }

    };
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "live-event-name" broadcasts.
    private BroadcastReceiver mLiveMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get Live results included in the Intent
            double res = intent.getDoubleExtra("live_results",0.0);
            Log.d("receiver", "Got message: " +res);
            resultsLive.setText(String.valueOf(res));

        }

    };

    private void askToEnableGPS(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, Please enable it so the app could work properly")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    protected void onDestroy() {
        //close DB
        measurementsDB.close();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLiveMessageReceiver);
        // In case the service is still running - back was pressed while recording
        if(isMyServiceRunning(MeasurementService.class)){
            stopService(new Intent(this, MeasurementService.class));
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // In case the service is still running - home was pressed while recording
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLiveMessageReceiver);
        if(isMyServiceRunning(MeasurementService.class)){
            stopService(new Intent(this, MeasurementService.class));
        }
        super.onPause();
    }

    /**  Check if service is running*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
