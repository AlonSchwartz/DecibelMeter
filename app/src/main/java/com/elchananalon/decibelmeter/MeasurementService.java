package com.elchananalon.decibelmeter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;


public class MeasurementService extends Service {
    private MediaRecorder mRecorder;
    private Handler mHandler = new Handler();
    private double liveDb = 0;
    private double maxDb = 0.0;

    private Runnable mPollTask = new Runnable() {
        @Override
        public void run() {
            liveDb = getAmplitude();
            // Send measurement object to activity via broadcast
            double toSend =Math.round(liveDb);
            if(toSend!=0.0){ // Make sure thread stops sending measurement if no input sound detected
                // Find maximum result for final result
                if(maxDb <= toSend){
                    maxDb =toSend;
                }
                // Send intent via broadcast
                Intent in = new Intent("live-event-name");
                in.putExtra("live_results",toSend);
                LocalBroadcastManager.getInstance(MeasurementService.this).sendBroadcast(in);
            }

            mHandler.postDelayed(mPollTask,300);

        }
    };
    private Runnable mSleepTask = new Runnable() {
        public void run() {

            startRecorder(); // Init media recorder
            //Noise monitoring start
            // Runnable(mPollTask) will execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, 300);
        }
    };
    private Thread mThread;
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Attach tasks to do in thread
        mThread = new Thread(mSleepTask);
        // Start running thread
        mThread.start();

        //we have some options for service
        //start sticky means service will be explicitly started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mRecorder != null){
            // Stopping the recorder when service is destroyed
            stopRecorder();

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            // Initialize media recorder
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "IllegalStateException called", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "prepare() failed", Toast.LENGTH_LONG).show();

            }
            mRecorder.start();

        }
    }
    public void stopRecorder() {
        if (mRecorder != null) {

            mHandler.removeCallbacks(mPollTask); // Remove pending posts
            mHandler.removeCallbacks(mSleepTask); // Remove pending posts
            mThread.interrupt(); // Kill thread
            // Stop media recorder
            mRecorder.stop();

            // Send measurement object to activity via broadcast
            double toSend =Math.round(maxDb);
            Intent in = new Intent("custom-event-name");
            in.putExtra("measurement_results",toSend);
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            // Free allocated memory for recorder
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;

        }
    }
    public double getAmplitude() {

        if (mRecorder != null) {
            // Cellphones can catch up to 90 db + -
            // getMaxAmplitude returns a value between 0-32767 (in most phones). that means that if the maximum db is 90, the pressure
            // at the microphone is 0.6325 Pascal.
            // it does a comparison with the previous value of getMaxAmplitude.
            // we need to divide maxAmplitude with (32767/0.6325)
            double f1 = mRecorder.getMaxAmplitude()/51805.5336;//51805.5336 or if 100db so 46676.6381
            if (f1>0) {
                //Assuming that the minimum reference pressure is 0.000085 Pascal (on most phones) is equal to 0 db
                return (Math.abs(20 * Math.log10(f1 / 0.000085)));
            }
            return 0;
        }
        else
            return 0;

    }
}
