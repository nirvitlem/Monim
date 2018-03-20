package com.vitlem.click2call;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by NirV on 28/12/2016.
 */

public class TimerService extends Service {

    @Override
    public void onCreate()
    {



        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("TimerService", "onStartCommand");

        Intent newIntent = new Intent( getApplicationContext(), MyService.class);
        if(MyService.ACTION_STATUS=="Working"){
            //that means your service is already running
            // stop your service here
            //context.stopService(newIntent);
            Log.d("ACTION_STATUS","Working");
        }else {
            //Its not running
            //Start your service here
            Log.d("restartService", "restartService");
            getApplicationContext().startService(newIntent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
