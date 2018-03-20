package com.vitlem.ilhwidget;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.appwidget.AppWidgetManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;
import android.content.ComponentName;

import com.widget.israelhayom.ilhwidget.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MyService extends Service {
    String t="ישראל היום";
    String tn="NRG";
    String ts="מעריב ספורט";
    static String l="http://www.israelhayom.co.il";
    static String n="http://www.nrg.co.il";
    static String ns="http://www.maariv.co.il";
    @Override
    public void onCreate()
    {



        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("debug", "onStartCommand");
        buildUpdate();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildUpdate()
    {
        Log.d("debug", "buildUpdate");
        String lastUpdated = DateFormat.format("d/M/yyyy HH:mm:ss", new Date()).toString();

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.w_class);

        view.setTextViewText(R.id.appwidget_text,"זמן עדכון "+ lastUpdated);

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;

//Percentage can be calculated for API 16+
        long percentAvail = ((mi.availMem*100) / mi.totalMem);

        view.setTextViewText(R.id.MemT,"משאבים " + availableMegs + " " +  percentAvail+"%" );

        view.setTextViewText(R.id.Tview, "TaG " + lastUpdated);
          Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ArrayList<PostData> aPostData = new ArrayList<PostData>();
                    RssDataController rdc = new RssDataController();

                    //aPostData = rdc.doInBackground(" http://rss.nrg.co.il/newsflash/");
                    aPostData = rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss");
                    //t= rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postTitle;
                   // l=rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postLink;
                    Date startDate ;
                    startDate = new Date(aPostData.get(0).postDate);//.substring(17,26) );

                    t=  DateFormat.format("d/M/yyyy HH:mm:ss", startDate).toString() + "\n " + aPostData.get(0).postTitle;
                    l=aPostData.get(0).postLink;
                   // ListItemsClass lc = new ListItemsClass();
                   // lc.setList();

                } catch (Exception e) {
                    t=e.getMessage() + " " +  DateFormat.format("d/M/yyyy HH:mm:ss", new Date()).toString();
                    e.printStackTrace();
                }
            }
                             });

        thread.start();
        view.setTextViewText(R.id.Tview, t);


        Thread threadn = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ArrayList<PostData> aPostData = new ArrayList<PostData>();
                    RssDataController rdc = new RssDataController();

                    aPostData = rdc.doInBackground("http://rss.nrg.co.il/news/");
                    //t= rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postTitle;
                    // l=rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postLink;
                    Date startDate ;
                    startDate = new Date(aPostData.get(0).postDate);//.substring(17,26) );
                    Calendar nd = Calendar.getInstance() ;
                    nd.setTime(startDate);
                    nd.add(Calendar.HOUR, 1);

                    tn=  DateFormat.format("d/M/yyyy HH:mm:ss", nd).toString() + "\n " + aPostData.get(0).postTitle;
                    n=aPostData.get(0).postLink;
                    // ListItemsClass lc = new ListItemsClass();
                    // lc.setList();

                } catch (Exception e) {
                    tn=e.getMessage() + " " +  DateFormat.format("d/M/yyyy HH:mm:ss", new Date()).toString();
                    e.printStackTrace();
                }
            }
        });

        threadn.start();
        view.setTextViewText(R.id.Tnview, tn);


        Thread threadns = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ArrayList<PostData> aPostData = new ArrayList<PostData>();
                    RssDataController rdc = new RssDataController();

                    //aPostData = rdc.doInBackground(" http://rss.nrg.co.il/newsflash/");
                    aPostData = rdc.doInBackground("http://www.maariv.co.il/Rss/RssFeedsSport?BeforeArticleID=0");
                    //t= rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postTitle;
                    // l=rdc.doInBackground("http://api.app.israelhayom.co.il/newsflash/rss").get(0).postLink;
                    Date startDate ;
                    startDate = new Date(aPostData.get(0).postDate);//.substring(17,26) );
                    Calendar nd = Calendar.getInstance() ;
                    nd.setTime(startDate);
                    nd.add(Calendar.HOUR, 1);
                    ts=  DateFormat.format("d/M/yyyy HH:mm:ss", nd).toString() + "\n " + aPostData.get(0).postTitle;
                    //ns=aPostData.get(0).postLink;
                    // ListItemsClass lc = new ListItemsClass();
                    // lc.setList();

                } catch (Exception e) {
                    ts=e.getMessage() + " " +  DateFormat.format("d/M/yyyy HH:mm:ss", new Date()).toString();
                    e.printStackTrace();
                }
            }
        });

        threadns.start();
        view.setTextViewText(R.id.Tsview, ts);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, wClass.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
    }



    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
