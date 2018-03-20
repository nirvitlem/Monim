package com.vitlem.click2call;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyService extends IntentService {
    private BroadcastReceiver mReceiver=null;
    public static String ACTION_STATUS =null;
    public static int SHAKE_STATUS=0;
    public MyService() {
        super("MyService");
    }


    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
  //  BroadcastReceiver mReceiver=null;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("onStartCommand", "onStartCommand");
               return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onHandleIntent(Intent i) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyBroadCastReciever();
        registerReceiver(mReceiver, filter);
        Log.i("ScreenOnOff", "create");
        try{
            // ShakeDetector initialization
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int countS) {
                    SHAKE_STATUS=countS;
                    Log.i("onShake", String.valueOf(countS));
                }
            });

            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        }
        catch (Exception e){
            Log.d("SnensorE",e.toString());

        }
        try {

            ACTION_STATUS="Working";
            while (true) {
            }
        }catch(Exception e) {
            ACTION_STATUS="Exception";
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_STATUS));}
        ACTION_STATUS="NotWorking";
    }


      @Override
    public void onDestroy() {

        Log.i("ScreenOnOff", "Service  distroy");
          ACTION_STATUS="onDestroy";
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);
          super.onDestroy();
    }
}
