package com.vitlem.nir.vperd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MainAppWidgetConfigureActivity MainAppWidgetConfigureActivity}
 */
public class MainAppWidget extends AppWidgetProvider {
    public static PendingIntent service = null;
    public static AlarmManager m=null;
    public static Calendar TIME=null;
    public static Intent i=null;
    static RemoteViews remoteViews;
    public static boolean runService = true;
    private static final String ClickToOff = "ClickToOffTag";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("debug", "onUpdate");

        CharSequence widgetText = MainAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d("debug", "onUpdate");
        final int N = appWidgetIds.length;
        remoteViews = new RemoteViews(
                context.getPackageName(),R.layout.main_app_widget);
        for (int i = 0; i < N; ++i) {

            //remoteViews.setOnClickPendingIntent(R.id.UButton, getPendingSelfIntent(context, ClickToOff));
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
if (runService)
{
    Log.d("CreateAlarm","CreateAlarm");
    m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    TIME = Calendar.getInstance();
    TIME.set(Calendar.MINUTE, 0);
    TIME.set(Calendar.SECOND, 0);
    TIME.set(Calendar.MILLISECOND, 0);

    i = new Intent(context, MyService.class);

    if (service == null) {
        service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 300 * 1000, service);
}

        Log.d("AppWidget", "onUpdate");


    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
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
        super.onReceive(context, intent);//add this line
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MainAppWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        Log.d("debug", "Somthing Clicked");
       // Log.d("i", String.valueOf(i));
       // Log.d("intent.getAction()",intent.getAction().toString());
        switch (intent.getAction().toString()) {
            case ClickToOff:
                // your onClick action is her
                RemoteViews remoteViewsF = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
                Log.d("debug", "ClickedOff");
                if (runService) {
                    Log.d("ClickedOff","SetOFF");
                    //remoteViewsF.setTextViewText(R.id.UButton, "הפעל");
                    m=null;
                    runService = false;
                    if (service!=null) service.cancel();
                    service=null;
                    i=null;

                } else {
                   // remoteViewsF.setTextViewText(R.id.UButton, "כבה");
                    Log.d("ClickedOff", "SetON");
                    runService = true;
                    m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    TIME = Calendar.getInstance();
                    TIME.set(Calendar.MINUTE, 0);
                    TIME.set(Calendar.SECOND, 0);
                    TIME.set(Calendar.MILLISECOND, 0);

                    i = new Intent(context, MyService.class);

                    if (service == null) {
                        service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                    }

                    m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 300 * 1000, service);

                }
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViewsF);
                break;

        }

    }
}

