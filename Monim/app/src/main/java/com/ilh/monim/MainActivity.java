package com.ilh.monim;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                WebView myWebView = (WebView) findViewById(R.id.MoninView);
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
            }
        });

        String Udid = android.provider.Settings.System.getString(super.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        //Udid ="7d361b31a68e9031";
        if (CheckUdid(Udid)) {
            WebView myWebView = (WebView) findViewById(R.id.MoninView);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            myWebView.loadUrl("http://62.0.30.244/?restricted=" + Udid);
        }
        else
        {
        }
    }

    private boolean CheckUdid(String Udid)
    {
        if (Udid.equals("32a3a9911ba528ad") || Udid.equals("d7b9bb80351f2c81") || Udid.equals("7d361b31a68e9031") ||
                Udid.equals("5c1b21dd658e36d8") || Udid.equals("2777001b41376134") ||
                Udid.equals("1b11567010d0ed55") || Udid.equals("1a262cd05c9c76fc") || Udid.equals("3db52682caefd996")|| Udid.equals("13fa29919cbe2894")| Udid.equals("8974f50812926b95"))
        {

            return true;
        }
        else {
            Intent intent = new Intent(this, ErrorActivity.class);
            ErrorActivity.Udid = Udid;
            startActivity(intent);
            return false;
        }

        //return false;
    }

}
