package com.elchananalon.decibelmeter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class Measurment extends AppCompatActivity implements View.OnClickListener{

    private Button buttonStart;
    private Button buttonStop;
    private Button buttonPlayLastRecordAudio;
    //creating a mediaplayer object
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurment);
        //getting buttons from xml
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonPlayLastRecordAudio = (Button)findViewById(R.id.buttonPlay);

        //attaching onclicklistener to buttons
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        /*buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                player = new MediaPlayer();
                try {
                    player.setDataSource(AudioSavePathInDevice);
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                player.start();
                Toast.makeText(Measurment.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });*/
    }
    public void onClick(View view)
    {
        if (view == buttonStart)
        {
            //starting service
            startService(new Intent(this, MeasurmentService.class));
        }

        if (view == buttonStop)
        {
            //stopping service - call onDestroy
            stopService(new Intent(this, MeasurmentService.class));
        }
    }
}
