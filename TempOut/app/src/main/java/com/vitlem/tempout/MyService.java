package com.vitlem.tempout;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class MyService extends Service {

       public MyService() {
    }

    @Override
    public void onCreate() {
    super.onCreate();
       Log.d("onCreate", "sc= new SensorActivity()");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand");

        buildUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate() {
        Log.d("debug", "buildUpdate");


        RemoteViews view = new RemoteViews(getPackageName(), R.layout.temp_widget);

        view.setTextViewText(R.id.appwidget_text,SensorActivity.getTemp() );
        Log.d("buildUpdate", SensorActivity.getTemp());

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, TempWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
