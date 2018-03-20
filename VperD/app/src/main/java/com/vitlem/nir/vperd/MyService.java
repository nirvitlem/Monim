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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private double xLoc =31.907368;
    private double yLoc=35.012348;
    public static float dis=0;
    public static AudioManager audio;
    public static Uri alertUri;
    public static Ringtone r;
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
        if (MainAppWidget.runService) BuildUpdate();
        return super.onStartCommand(intent, flags, startId);
    }


    private void BuildUpdate()
    {
        Log.d("debug", "buildUpdate");
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(), alertUri);
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
                        lb.setLongitude(yLoc);
                        dis=  location.distanceTo(lb)/1000;
                        StatusM= "Lat " + latitude  + " \nLon " + longitude + " \ndistance " + String.valueOf(dis);
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

       // view.setTextViewText(R.id.appwidget_text, StatusM + " \nAM " + getVoulumeP(dis) +" \nTimeUpdate  " + Calendar.getInstance().getTime());
        view.setTextViewText(R.id.appwidget_text, StatusM + " \nAM " + " \nTimeUpdate  " + Calendar.getInstance().getTime());
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

    static void runGetVolumep()
    {
        Log.i("runGetVolumep", "runGetVolumep");
        new MyService().getVoulumeP(dis) ;
        Log.i("runGetVolumep", String.valueOf(dis));
        Log.i("runGetVolumep", "runGetVolumep");
    }

    static void StopPalyPlayer()
    {
        Log.i("StopPalyPlayer", "StopPalyPlayer");
        if(r.isPlaying()){
            Log.i("StopPalyPlayer", "PlayerisPlaying");
            r.stop();

        }else
        {
            Log.i("StopPalyPlayer", "PlayerisNOTPlaying");
        }
    }

    private double getVoulumeP(float dist)
    {
        try {
            Log.i("getVoulumeP", String.valueOf(dist));
            //audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

// Get the current ringer volume as a percentage of the max ringer volume.
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
            Log.i("currentVolume", String.valueOf(currentVolume));
            int maxRingerVolume = audio.getStreamMaxVolume(AudioManager.STREAM_RING);
            Log.i("maxRinger", String.valueOf(maxRingerVolume));
            double proportion = currentVolume / (double) maxRingerVolume;
            Log.i("proportion", String.valueOf(proportion));

// Calculate a desired music volume as that same percentage of the max music volume.
            int maxMusicVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int desiredMusicVolume = (int) (proportion * maxMusicVolume);

// Set the music stream volume.
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, desiredMusicVolume, 0 /*flags*/);
            Log.i("getVoulumeP", String.valueOf(dist));
            if(dist>1)
            {
                if (proportion<0.5)
                {
                    audio.setStreamVolume(AudioManager.STREAM_RING,maxRingerVolume,AudioManager.FLAG_PLAY_SOUND);

                    if(r != null && !r.isPlaying()){
                        r.play();

                    }
                }
            }

            return proportion;
        }
        catch(Exception e)
        {
            Log.i("getVoulumeP", e.getMessage());
            return  0;}
    }

}

