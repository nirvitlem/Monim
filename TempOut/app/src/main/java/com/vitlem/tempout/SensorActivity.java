package com.vitlem.tempout;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by NirV on 23/06/2016.
 */

public class SensorActivity  extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mTemp;
    private static float Temp;
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.temp_widget);

        Log.d("SensorActivity","onCreate");
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d("mTemp",mTemp.toString());
    }

    public static String getTemp()
    {
        return String.valueOf(Temp);
    }

       @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
           Log.d("SensorActivity","onAccuracyChanged");
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Log.d("SensorActivity","onSensorChanged");
        Temp = event.values[0];
        Log.d("temp",String.valueOf(Temp));
        /*
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.temp_widget);
        views.setTextViewText(R.id.appwidget_text, String.valueOf(Temp));*/
        // Do something with this sensor data.
    }

    @Override
    protected void onResume() {
        Log.d("SensorActivity","onResume");
        // Register a listener for the sensor.
        super.onResume();

        mSensorManager.registerListener(this, mTemp, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        Log.d("SensorActivity","onPause");
        super.onPause();

        mSensorManager.unregisterListener(this);
    }
}
