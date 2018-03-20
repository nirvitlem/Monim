package com.vitlem.click2call;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

/**
 * Created by NirV on 27/12/2016.
 */

public class MyBroadCastReciever extends BroadcastReceiver {

    private static boolean Vc = false;
    public static Timer myTimer = new Timer();
    private static Timer StartTimer = new Timer();
    public static Context sc;
    public static int countIoffon = 0;
    private Context mContext;
    private static final String TAG = "CustomBroadcastReceiver";
    TelephonyManager telephony;
    CustomPhoneStateListener customPhoneListener ;
    private static int CallState;



    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                Log.d("DEBUG", String.valueOf(state));

                telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                customPhoneListener = new CustomPhoneStateListener();
                telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                Bundle bundle = intent.getExtras();
                String phoneNr = bundle.getString("incoming_number");


            }
        }catch(Exception e)
        {
            Log.d("Exc",e.getMessage());
        }
        Log.d("BStarCountingBeforeCall", String.valueOf(CallState));
        Log.d("BStarCountingBeforeCall", String.valueOf(MyService.SHAKE_STATUS));
        if  ((CallState==CALL_STATE_IDLE) && (MyService.SHAKE_STATUS>2))
            {
                Log.d("StarCountingBeforeCall", String.valueOf(CallState));
                Log.d("StarCountingBeforeCall", String.valueOf(MyService.SHAKE_STATUS));
              //  if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
              //          TelephonyManager.EXTRA_STATE_IDLE)) {
                    //Log.d("StarCountingBeforeCall", "EXTRA_STATE_IDLE");
                    Vc = true;
                    StartTimer.cancel();
                    StartTimer = new Timer();
                    StartTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            StartTimerMethod();
                        }

                    }, 1000, 1000);


                    if ((intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) || (intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {
                        startTimer();
                        Log.d("countIoff ", String.valueOf(countIoffon));
                        //Take count of the screen off position
                    }
                    sc = context;
               // }
            }


    }


    public interface ReceiverListener {
        void received();
    }

    public class CustomPhoneStateListener extends PhoneStateListener
    {
        private static final String TAG = "CustomPhoneStateListener";
        Handler handler=new Handler();
        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch (state)
            {
                case CALL_STATE_RINGING:
                    if(!incomingNumber.equalsIgnoreCase(""))
                    {
                       Log.d("CALL_STATE_RINGING","CALL_STATE_RINGING");
                        CallState=CALL_STATE_RINGING;

                    }
                    break;
                case CALL_STATE_OFFHOOK:
                    Log.d("CALL_STATE_OFFHOOK","CALL_STATE_OFFHOOK");
                    CallState=CALL_STATE_OFFHOOK;
                    break;
                case CALL_STATE_IDLE:
                    Log.d("CALL_STATE_IDLE","CALL_STATE_IDLE");
                    //YOUR CODE HERE
                    CallState=CALL_STATE_IDLE;
                    break;
                default:
                    CallState=444;
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_NONE);
        }


    }

    private void startTimer() {
        Log.d("CATEGORY_HOME CallTimer", String.valueOf(Vc));
        if (Vc) {
            Log.d("CATEGORY_HOME CallTimer", String.valueOf(Vc));
            try {
                Log.d("PowerB CallTimer", String.valueOf(countIoffon));
                myTimer.cancel();
                myTimer = new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod();
                    }

                }, 1500, 1500);

                countIoffon++;
                Log.d("PowerB CallNum", String.valueOf(countIoffon));
                Log.d("PowerB", "MakeCall");

            } catch (ActivityNotFoundException e) {
                Log.d("Error", e.getMessage());
            }
        }

    }

    private void TimerMethod() {
        try {
            //This method is called directly by the timer
            //and runs in the same thread as the timer.
            if (countIoffon > 0) {
                //We call the method that will work with the UI
                //through the runOnUiThread method.
                Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                my_callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("TimerMethod Check", "check Per");
                // Log.d("TimerMethod Permission",String.valueOf(hasPermissionInManifest(sc,CALL_PHONE)));

                Log.d("TimerMethod", "MakeCall");
                Log.d("TimerMethod CallNum", String.valueOf(countIoffon));

                switch (countIoffon) {
                    case 1:

                        my_callIntent.setData(Uri.parse("tel:" + MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN1)));
                        sc.startActivity(my_callIntent);
                        Log.d("TimerMethod MakeCall ", "1");
                        break;
                    case 2:

                        my_callIntent.setData(Uri.parse("tel:" + MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN2)));
                        sc.startActivity(my_callIntent);
                        Log.d("TimerMethod MakeCall", "2");
                        break;
                    case 3:

                        my_callIntent.setData(Uri.parse("tel:" + MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN3)));
                        sc.startActivity(my_callIntent);
                        Log.d("TimerMethod MakeCall", "3");
                        break;
                    case 4:

                        my_callIntent.setData(Uri.parse("tel:" + MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN4)));
                        sc.startActivity(my_callIntent);
                        Log.d("TimerMethod MakeCall", "3");
                        break;
                }

                //here the word 'tel' is important for making a call...
            }
            countIoffon = 0;
            myTimer.cancel();
            myTimer = new Timer();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());

            countIoffon = 0;
            myTimer.cancel();
        }
    }
    private void StartTimerMethod() {
        Vc=false;
        MyService.SHAKE_STATUS=0;
        StartTimer.cancel();


    }
}