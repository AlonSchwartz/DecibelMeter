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


public class DecibelMeasurement extends AppCompatActivity implements View.OnClickListener{

    private Button buttonStart;
    private Button buttonStop;
    private TextView results;
    private Locator loc;
    private Measurement measurement;
    private String place;

    private SQLiteDatabase measurementsDB = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};


    private boolean permissionToRecordAccepted = false;
    private boolean permissionToTrackAccepted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_decibel_measurement);
        //getting buttons from xml
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        results = (TextView) findViewById(R.id.results);
        //attaching on click listeners to buttons
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        // Ask for permissions to use microphone & gps if does not have permissions
        ActivityCompat.requestPermissions(this, permissions, 0);



        // Init Database
        measurementsDB = openOrCreateDatabase("Measurements", MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS measurements (id integer primary key, location VARCHAR, timeTaken VARCHAR, result VARDOUBLE);";
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
            finish();
        }
    }

    public void onClick(View view)
    {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        loc  = new Locator(this);

        if (view == buttonStart)
        {
            //starting service and getting location & time

            place = loc.trackLocation();

            Log.d("Debug", "=================================================>>>>>>>>>"+place);
            startService(new Intent(this, MeasurementService.class));
        }

        if (view == buttonStop)
        {
            //stopping service - call onDestroy
            stopService(new Intent(this, MeasurementService.class));

        }
    }
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double resu[] = intent.getDoubleArrayExtra("measurement_results");
            Log.d("receiver", "Got message: " +resu[0]+"  "+resu[1]+"  "+resu[2]);
            //results.setText(""+meas.getAmplitude());
            String time = new Timestamp(System.currentTimeMillis()).toString();
            measurement = new Measurement(resu[0],place,time);
            String update = "INSERT INTO measurements (location, timeTaken, result) VALUES ('" + measurement.getPlace() + "', '" + measurement.getCurr_time() + "', '" + measurement.getDb() +  "');";
            measurementsDB.execSQL(update);
            results.setText("Results = "+measurement.getDb()+" db, Time: "+ measurement.getCurr_time() + " Place: \n" + measurement.getPlace());
        }
    };
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
