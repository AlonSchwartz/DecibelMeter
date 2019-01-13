package com.elchananalon.decibelmeter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;



public class MeasurmentService extends Service {
    private Measurment meas;
    private MediaRecorder mRecorder;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    private boolean isRunning;


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        startRecorder();
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                isRunning = true;
                int i = 0;
                while(isRunning)
                {
                    Log.d("debug","i="+i);
                    i++;
                    SystemClock.sleep(5000);
                }
            }
        }).start();
        Log.d("debug","onStartCommand()");

        //we have some options for service
        //start sticky means service will be explicitly started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mRecorder != null){
            //stopping the recorder when service is destroyed
            stopRecorder();
            Log.d("debug","MyService onDestroy()");
        }
        isRunning = false;

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
            mRecorder.setAudioChannels(1);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
            try
            {
                mRecorder.prepare();
            }
            catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            Log.d("debug","recording started");
              //mEMA = 0.0;
        }
    }
    public void stopRecorder() {
        if (mRecorder != null) {

            mRecorder.stop();

            // meas = new Measurment(mRecorder);
            // Send measurement object to activity via broadcast
            double toSend[] ={getAmplitude(),getAmplitudeEMA(),soundDb(getAmplitude())};
            Intent intent = new Intent("custom-event-name");
            intent.putExtra("measurement_results",toSend);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


            mRecorder.reset();
            mRecorder.release();// if removed no segmentation fault but crashes after second play
            mRecorder = null;
            Log.d("debug","recording stopped");
        }
    }
    public double soundDb(double ampl){
        if(ampl == 0){
            return 0;
        }
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
