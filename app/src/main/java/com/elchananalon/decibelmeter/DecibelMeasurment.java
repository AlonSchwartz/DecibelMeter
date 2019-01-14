package com.elchananalon.decibelmeter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DecibelMeasurment extends AppCompatActivity implements View.OnClickListener{

    private Button buttonStart;
    private Button buttonStop;
    private TextView results;
    private Measurment meas;
    //private Button buttonPlayLastRecordAudio;
    //creating a mediaplayer object
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decibel_measurment);
        //getting buttons from xml
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        results = (TextView) findViewById(R.id.results);
        //attaching onclicklistener to buttons
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);



    }
    public void onClick(View view)
    {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));


        if (view == buttonStart)
        {
            //starting service
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        0);

            }

                startService(new Intent(this, MeasurmentService.class));

        }

        if (view == buttonStop)
        {
            //stopping service - call onDestroy
            stopService(new Intent(this, MeasurmentService.class));

        }
    }
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            //meas = (Measurment)intent.getSerializableExtra("measurement_results");
            double resu[] = intent.getDoubleArrayExtra("measurement_results");
            Log.d("receiver", "Got message: " +resu[0]+"  "+resu[1]+"  "+resu[2]);
            //results.setText(""+meas.getAmplitude());
            results.setText("DB = "+resu[0]+" , AmplitudeEMA= "+resu[1]);
        }
    };
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
