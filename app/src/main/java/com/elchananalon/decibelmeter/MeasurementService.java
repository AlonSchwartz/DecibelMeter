package com.elchananalon.decibelmeter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


public class MeasurementService extends Service {
    private MediaRecorder mRecorder;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    private boolean isRunning = true;
    private Handler mHandler = new Handler();
    private double db = 0;

    private Runnable mPollTask = new Runnable() {
        @Override
        public void run() {
            db = getAmplitude();

            mHandler.postDelayed(mPollTask,300);
            Log.d("Thread","=====================My ID is: "+android.os.Process.getThreadPriority(android.os.Process.myTid()));

        }
    };
    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            startRecorder();
            //Noise monitoring start
            // Runnable(mPollTask) will execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, 300);
        }
    };
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        startRecorder();

//        new Thread(new Runnable() {
//            @Override
//            public void run()
//            {
//                isRunning = true;
//                //while(isRunning)
//                //{
//
//
//                    //int i = 0;
//
//                    //Log.d("debug","i="+i);
//                    //i++;
//                    SystemClock.sleep(300);
//                //}
//            }
//        }).start();
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
            try
            {
                mRecorder.prepare();
            } catch (IllegalStateException e) {
                Toast.makeText(getApplicationContext(), "IllegalStateException called", Toast.LENGTH_LONG).show();


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "prepare() failed", Toast.LENGTH_LONG).show();

            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            Log.d("debug","recording started");

            mEMA = 0.0;
            mHandler.postDelayed(mPollTask,300);
        }
    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mHandler.removeCallbacks(mPollTask);
            mHandler.removeCallbacks(mSleepTask);
            mRecorder.stop();

            // Send measurement object to activity via broadcast
            double toSend[] ={Math.round(db)};


            Intent in = new Intent("custom-event-name");
            in.putExtra("measurement_results",toSend);
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);


            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            Log.d("debug","recording stopped");
        }
    }
    //    public double soundDb(double ampl){
//        if(ampl == 0){
//            return 0;
//        }
//        return  20 * Math.log10(getAmplitudeEMA() / ampl);
//    }
    public double getAmplitude() {

        if (mRecorder != null) {
            //Cellphone can catch up to 90 db + - ,
            double f1 = mRecorder.getMaxAmplitude()/51805.5336;
            if (f1>0) {
                Log.d("Debug", "=======> " + 20 * Math.log10(f1 / 0.0002) + " maxAMp: " + f1);
                return (20 * Math.log10(f1 / 0.0002));
            }
            return 0;
        }
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
