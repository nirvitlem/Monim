package com.vitlem.ilhwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.widget.israelhayom.ilhwidget.R;

import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link wClassConfigureActivity wClassConfigureActivity}
 */
public class wClass extends AppWidgetProvider {

    private static final String ClickToHear = "ClickToHearTag";
    private static final String ClickToUpdate = "ClickToUpdateTag";
    private static final String ClickToFlash = "ClickToFlashTag";
    private static final String ClickToLink = "ClickToLinkTag";
    private static final String ClickToLinkn = "ClickToLinkTagn";
    private static final String ClickToLinks = "ClickToLinkTags";
    private PendingIntent service = null;
    static int index = 0;
    static MediaPlayer player = new MediaPlayer();
    static RemoteViews remoteViews;
    public static Camera cam;// = Camera.open();


    public enum RSSXMLTag {
        TITLE, DATE, LINK, CONTENT, GUID, IGNORETAG;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    //int[] appWidgetIds holds ids of multiple instance
 /** of your widget
 * meaning you are placing more than one widgets on
 * your homescreen*/
        Log.d("debug", "onUpdate");
        final int N = appWidgetIds.length;
            for (int i = 0; i < N; ++i) {
                remoteViews = updateWidgetListView(context, appWidgetIds[i]);
               // Log.d("Debug",appWidgetIds[i] + " " + appWidgetIds.toString());
                remoteViews.setOnClickPendingIntent(R.id.VButton, getPendingSelfIntent(context, ClickToHear));
                remoteViews.setOnClickPendingIntent(R.id.UButton, getPendingSelfIntent(context, ClickToUpdate));
                remoteViews.setOnClickPendingIntent(R.id.FButton, getPendingSelfIntent(context, ClickToFlash));
                remoteViews.setOnClickPendingIntent(R.id.Tview, getPendingSelfIntent(context, ClickToLink));
                remoteViews.setOnClickPendingIntent(R.id.Tnview, getPendingSelfIntent(context, ClickToLinkn));
                remoteViews.setOnClickPendingIntent(R.id.Tsview, getPendingSelfIntent(context, ClickToLinks));

                appWidgetManager.updateAppWidget(appWidgetIds[i],
                        remoteViews);
            }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, MyService.class);

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 100, service);

       }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private RemoteViews updateWidgetListView(Context context,
                                             int appWidgetId) {

        //which layout to show on widget
         remoteViews = new RemoteViews(
               context.getPackageName(),R.layout.w_class);
Log.d("Debug","updateWidgetListView");
        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
       /* remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
                svcIntent);
        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);*/

        return remoteViews;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0, appWidgetIdsLength = appWidgetIds.length; i < appWidgetIdsLength; i++) {
            int appWidgetId = appWidgetIds[i];
            wClassConfigureActivity.deleteTitlePref(context, appWidgetId);
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = wClassConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews remoteViewsD = new RemoteViews(context.getPackageName(), R.layout.w_class);
        remoteViewsD.setTextViewText(R.id.appwidget_text, widgetText + " " + Integer.toString(index++));

        //
            // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context,
                wClass.class);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), wClass.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        //Log.d("debug", "Somthing Clicked");
        switch(intent.getAction().toString())
        {
            case ClickToHear:
                Log.d("debug", "Clicked");
                Log.d("debug", String.valueOf(player.getAudioSessionId()));
                RemoteViews remoteViewsH = new RemoteViews(context.getPackageName(), R.layout.w_class);
                try {
                    if (player.isPlaying()) {
                        Log.d("debug", "IsPlaying");
                        player.stop();
                        player.reset();
                        remoteViewsH.setTextViewText(R.id.VButton, "שמע");
                        //player = null;
                    } else {
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource("http://kolfix.israelhayom.co.il/webreader.audio/israelhayom-news_n_sivan.mp3");
                        player.prepare();
                        player.start();
                        remoteViewsH.setTextViewText(R.id.VButton, "הפסק");


                    }
                } catch (Exception e) {
                    Log.d("Error", e.getMessage().toString());
                }

                appWidgetManager.updateAppWidget(appWidgetIds, remoteViewsH);
                break;
            case ClickToUpdate:
                // your onClick action is here
                Log.d("debug", "ClickedUpdate");

                final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                final Calendar TIME = Calendar.getInstance();
                TIME.set(Calendar.MINUTE, 0);
                TIME.set(Calendar.SECOND, 0);
                TIME.set(Calendar.MILLISECOND, 0);

                final Intent i = new Intent(context, MyService.class);

                if (service == null)
                {
                    service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                }

                m.set(AlarmManager.RTC, 1, service);

               // m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 100, service);
                break;
            case ClickToLink:
                // your onClick action is here
                Log.d("debug", "ClickedLink");

                try {
                    Intent iK = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(MyService.l));
                    iK.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iK);
                    Log.d("debug", MyService.l);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage().toString());
                }
                break;
            case ClickToLinkn:
                // your onClick action is here
                Log.d("debug", "ClickedLinkn");

                try {
                    Intent iN = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(MyService.n));
                    iN.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iN);
                    Log.d("debug", MyService.n);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage().toString());
                }
                break;
            case ClickToLinks:
                // your onClick action is here
                Log.d("debug", "ClickedLinks");

                try {
                    Intent iS = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(MyService.ns));
                    iS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(iS);
                    Log.d("debug", MyService.ns);
                } catch (Exception e) {
                    Log.d("Error", e.getMessage().toString());
                }
                break;
            case ClickToFlash:
                RemoteViews remoteViewsF = new RemoteViews(context.getPackageName(), R.layout.w_class);
                Log.d("debug", "ClickedFlash");
                if (cam==null) cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                Log.d("Flash",p.getFlashMode());
                if (p.getFlashMode().equals("off")) {
                    Log.d("Flash","SetOn");
                    remoteViewsF.setTextViewText(R.id.FButton, "כבה");
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                } else {
                    if (p.getFlashMode().equals("torch")) {
                        remoteViewsF.setTextViewText(R.id.FButton, "פנס");
                        Log.d("Flash","SetOff");
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        cam.setParameters(p);
                        cam.startPreview();
                        cam.stopPreview();
                        cam.release();
                        cam=null;
                    }
                }
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViewsF);
                break;
        }

    }

}


