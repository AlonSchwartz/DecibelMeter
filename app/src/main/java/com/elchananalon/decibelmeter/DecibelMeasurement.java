package com.elchananalon.decibelmeter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.TextView;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DecibelMeasurement extends AppCompatActivity implements View.OnClickListener{

    private Button buttonStart;
    private TextView results;
    //private Locator loc;
    private Measurement measurement;
    private String place;
    private String[] locResults;
    private String waypoints;

    private SQLiteDatabase measurementsDB = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    boolean isMeasuring = false;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToTrackAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_decibel_measurement);
        //getting buttons from xml
        buttonStart = findViewById(R.id.buttonStart);
        results = findViewById(R.id.txt_results);
        //attaching on click listeners to buttons
        buttonStart.setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for API greater than 23, because requesting for permissions is mandatory since then.


        // Ask for permissions to use microphone & gps if does not have permissions
        ActivityCompat.requestPermissions(this, permissions, 0);

        }

        // Init Database
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE, waypoints VARCHAR);";
        measurementsDB.execSQL(sql);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
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
            finish();
        }
    }

    public void onClick(View v)
    {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        Locator loc  = new Locator(this);
        Log.d("Thread","=====================My ID is: "+android.os.Process.getThreadPriority(android.os.Process.myTid()));


        switch(v.getId()){

            case R.id.buttonStart:
                startService(new Intent(this, MeasurementService.class));

                if (!isMeasuring) {
                    buttonStart.setText("Stop");

                } else {
                    buttonStart.setText("Start");
                    stopService(new Intent(this, MeasurementService.class));
                    locResults = loc.trackLocation();
                    place = locResults[0];
                    waypoints = locResults[1];


                }
                isMeasuring = !isMeasuring;



                break;
        }


    }
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double resu[] = intent.getDoubleArrayExtra("measurement_results");
            Log.d("receiver", "Got message: " +resu[0]);
            //results.setText(""+meas.getAmplitude());

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
            Date d = new Date(System.currentTimeMillis());
            String time = dateFormat.format(d);

            //String time = new Timestamp(System.currentTimeMillis()).toString();

            //String time = String.format("%1$tr", System.currentTimeMillis());

            String e = (place.replaceAll("-", " ").replaceAll("'", "")); //Delete - and ' from strings, they are doing errors when uploading to SQLite
            Log.d("Change:", ""+ e);
            measurement = new Measurement(resu[0],e,waypoints, time);
            String update = "INSERT INTO measurements (location, timeTaken, result, waypoints) VALUES ('" + measurement.getPlace() + "', '" + measurement.getCurr_time() + "', '" + measurement.getDb() +  "', '" + measurement.getWaypoints()+"');";
            measurementsDB.execSQL(update);
            results.setText("Results: "+measurement.getDb()+" db\nTime: \n"+ measurement.getCurr_time() + " \nPlace: \n" + measurement.getPlace());

        }

    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
