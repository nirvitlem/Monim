package com.vitlem.click2call;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MainAppWidgetConfigureActivity MainAppWidgetConfigureActivity}
 */
public class MainAppWidget extends AppWidgetProvider {
    public static String ClickOnME= "ClickW";
    public static int callNum = 0;
    public static Timer myTimer=new Timer();
    public static Context sc;
    public static CharSequence N1Text;
    public static CharSequence N2Text;
    public static CharSequence N3Text;
    public static CharSequence N4Text;

    public PendingIntent service = null;
    public static Intent confIntent;
    public static String ACTION_WIDGET_CONFIGURE = "MainAppWidgetConfigureActivity";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("updateAppWidget", "updateAppWidget");
        CharSequence widgetText = "";

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
        widgetText = MainAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        N1Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(context, R.id.EditTextN1);
        N2Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(context, R.id.EditTextN2);
        N3Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(context, R.id.EditTextN3);
        N4Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(context, R.id.EditTextN4);


        // Construct the RemoteViews object
        //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setInt(R.id.appwidget_text,"setBackgroundResource",MainAppWidgetConfigureActivity.loadBackGroundPref(context, R.id.appwidget_text));
        views.setTextViewText(R.id.EditTextN1, N1Text);
        views.setTextViewText(R.id.EditTextN2, N2Text);
        views.setTextViewText(R.id.EditTextN3, N3Text);
        views.setTextViewText(R.id.EditTextN4, N4Text);



        Intent intent = new Intent(context, MainAppWidget.class);
        intent.setAction(ClickOnME);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
      /*  final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, MyService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 100, service);*/

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, TimerService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 100, service);



        Log.d("AppWidget","onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MainAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        sc=context;

        if (intent.getAction().equals(ClickOnME)) {

            Intent newIntent = new Intent(context, MyService.class);
            if(MyService.ACTION_STATUS=="Working"){
                //that means your service is already running
                // stop your service here
                //context.stopService(newIntent);
                Log.d("ACTION_STATUS","Working");
            }else {
                //Its not running
                //Start your service here
                Log.d("restartService", "restartService");
                context.startService(newIntent);
            }
            try {
                Log.d("onReceive CallTimer", String.valueOf(callNum));
                myTimer.cancel();
                myTimer=new Timer();
                myTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TimerMethod();
                    }

                }, 1500, 1500);

                callNum++;
                Log.d("onReceive CallNum", String.valueOf(callNum));
                Log.d("onReceive", "MakeCall");

            } catch (ActivityNotFoundException e) {
                Log.d("Error", e.getMessage());
            }
        }
    }

    private void TimerMethod() {
        try {


            //We call the method that will work with the UI
            //through the runOnUiThread method.
            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
            my_callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("TimerMethod Check","check Per");
           // Log.d("TimerMethod Permission",String.valueOf(hasPermissionInManifest(sc,CALL_PHONE)));

            Log.d("TimerMethod", "MakeCall");
            Log.d("TimerMethod CallNum", String.valueOf(callNum));

            switch ( callNum) {
                case 1:

                    my_callIntent.setData(Uri.parse("tel:" + MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN1)));
                    sc.startActivity(my_callIntent);
                    Log.d("TimerMethod MakeCall "  , "1");
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
            callNum = 0;
            myTimer.cancel();
            myTimer=new Timer();
            //here the word 'tel' is important for making a call...

        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
          /*  N1Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN1);
            N2Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN2);
            N3Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN3);
            N4Text = MainAppWidgetConfigureActivity.loadTitlePrefNum0(sc, R.id.EditTextN4);*/
            callNum = 0;
            myTimer.cancel();
        }

    }

    /*public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {

                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }*/



}

