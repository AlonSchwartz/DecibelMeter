package com.elchananalon.decibelmeter;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;

public class MeasurmentService extends Service {

    private MediaRecorder mRecorder;
    private boolean isRunning;

    private String AudioSavePathInDevice = null;
    private Random random ;
    private String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        random = new Random();
        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        CreateRandomAudioFileName(5) + "AudioRecording.3gp";
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
                    SystemClock.sleep(1000);
                }
            }
        }).start();
        Log.d("debug","MyService onStartCommand()");

        //we have some options for service
        //start sticky means service will be explicitly started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //stopping the recorder when service is destroyed
        stopRecorder();
        Log.d("debug","MyService onDestroy()");
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(AudioSavePathInDevice);
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

              //mEMA = 0.0;
        }
    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
         //mStatusView.setText(Double.toString((getAmplitudeEMA())) + " dB");
    }
    public double soundDb(double ampl){
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
    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    /**************************
     * private TextView mStatusView;
     * private tMediaRecorder mRecorder;
     * Thread runner;
     * private static double mEMA = 0.0;
     * static final private double EMA_FILTER = 0.6;
     *
     * final Runnable updater = new Runnable(){
     *
     *     public void run(){
     *         updateTv();
     *     };
     * };
     * final Handler mHandler = new Handler();
     *
     * public void onCreate(Bundle savedInstanceState) {
     *     super.onCreate(savedInstanceState);
     *
     *     setContentView(R.layout.noiselevel);
     *     mStatusView = (TextView) findViewById(R.id.status);
     *
     *
     *     if (runner == null)
     *     {
     *         runner = new Thread(){
     *             public void run()
     *             {
     *                 while (runner != null)
     *                 {
     *                     try
     *                     {
     *                         Thread.sleep(1000);
     *                         Log.i("Noise", "Tock");
     *                     } catch (InterruptedException e) { };
     *                     mHandler.post(updater);
     *                 }
     *             }
     *         };
     *         runner.start();
     *         Log.d("Noise", "start runner()");
     *     }
     * }
     */
}
