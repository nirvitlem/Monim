package com.vitlem.nir.vperd;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by NirV on 07/08/2017.
 */

public class MyService extends Service {
    private BroadcastReceiver mReceiver=null;
    public static String ACTION_STATUS =null;
    public static String StatusM= "";
    private static  boolean GPSorN= true;
    private double xLoc =31.907251;
    private double yLoc=35.012393;
    TelephonyManager tManager;

    @Override
    public void onCreate()
    {



        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("onStartCommand", "onStartCommand");
        BuildUpdate();
        return super.onStartCommand(intent, flags, startId);
    }


    private void BuildUpdate()
    {
        Log.d("debug", "buildUpdate");
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.main_app_widget);
        tManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE) ;
        tManager.listen(new CustomPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE
        );
        LocationManager locationManager;
        boolean isGPSEnabled = false;
        Location location=null; // location
        double latitude=0; // latitude
        double longitude=0; // longitude
        // The minimum distance to change Updates in meters
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


        // Instruct the widget manager to update the widget
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;

        }

        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isGPSEnabled) {
            if (location == null) {
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    Log.d("locationManager","LocationManager is not null");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location==null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        GPSorN=false;
                    }
                    else GPSorN=true;
                    if (location != null) {
                        Log.d("location","Location is not null");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("latitude",String.valueOf(latitude)+ " TimeUpdate  " + Calendar.getInstance().getTime());
                        Log.d("longitude",String.valueOf(longitude)+ " TimeUpdate  " + Calendar.getInstance().getTime());
                        //view.setInt(R.layout.main_app_widget,"setBackgroundColor", Color.GREEN);
                        if (GPSorN) view.setTextColor(R.id.appwidget_text, Color.GREEN); else  view.setTextColor(R.id.appwidget_text, Color.MAGENTA);
                        Location lb= new Location("point B");
                        lb.setLatitude(xLoc);
                        lb.setAltitude(yLoc);
                        StatusM= "Lat " + latitude  + " Lon " + longitude + " distance " + String.valueOf(location.distanceTo(lb));
                    }
                    else
                    {
                        view.setTextColor(R.id.appwidget_text, Color.RED);
                        StatusM="Location is Null";
                        //view.setInt(R.layout.main_app_widget,"setBackgroundColor", Color.RED);
                    }
                }
            }
            else
            {
                view.setTextColor(R.id.appwidget_text, Color.RED);
                StatusM="Location Manager is Null";
                //view.setInt(R.layout.main_app_widget,"setBackgroundColor", Color.RED);
            }
        }
        else
        {
            view.setTextColor(R.id.appwidget_text, Color.RED);
            StatusM="GPS Is not Enabled";
            //view.setInt(R.layout.main_app_widget,"setBackgroundColor", Color.RED);
        }

        view.setTextViewText(R.id.appwidget_text, StatusM + " AM " + getVoulumeP() +" TimeUpdate  " + Calendar.getInstance().getTime());
        ComponentName thisWidget = new ComponentName(this, MainAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @Override
    public void onDestroy() {

        Log.i("ScreenOnOff", "Service  distroy");
        ACTION_STATUS="onDestroy";
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private double getVoulumeP()
    {
        try {
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

// Get the current ringer volume as a percentage of the max ringer volume.
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
            int maxRingerVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
            double proportion = currentVolume / (double) maxRingerVolume;

// Calculate a desired music volume as that same percentage of the max music volume.
            int maxMusicVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int desiredMusicVolume = (int) (proportion * maxMusicVolume);

// Set the music stream volume.
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, desiredMusicVolume, 0 /*flags*/);
            return proportion;
        }
        catch(Exception e)
        {return  0;}
    }

}

