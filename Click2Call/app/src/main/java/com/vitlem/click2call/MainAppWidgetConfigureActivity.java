package com.vitlem.click2call;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RemoteViews;

/**
 * The configuration screen for the {@link MainAppWidget MainAppWidget} AppWidget.
 */
public class MainAppWidgetConfigureActivity extends Activity {
    public static  String CALL_PHONE="CALL_PHONE";
    public final int MY_PERMISSIONS_REQUEST_CALL_PHONE=1;


    private static final String PREFS_NAME = "com.vitlem.click2call.MainAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;


            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    final Context context = MainAppWidgetConfigureActivity.this;

                    RadioButton rbw = (RadioButton) findViewById(R.id.radioButtonRec);

                    // When the button is clicked, store the string locally
                    String widgetText = mAppWidgetText.getText().toString();
                    saveTitlePref(context, mAppWidgetId, widgetText);



                    EditText tvN0;
                    tvN0 = (EditText) findViewById(R.id.EditTextN1);
                    Log.d("tvN0", tvN0.getText().toString());
                    saveTitleNum0(context, R.id.EditTextN1, tvN0.getText().toString());

                    EditText tvN1;
                    tvN1 = (EditText) findViewById(R.id.EditTextN2);
                    Log.d("tvN0", tvN1.getText().toString());
                    saveTitleNum0(context, R.id.EditTextN2, tvN1.getText().toString());

                    EditText tvN2;
                    tvN2 = (EditText) findViewById(R.id.EditTextN3);
                    Log.d("tvN0", tvN2.getText().toString());
                    saveTitleNum0(context, R.id.EditTextN3, tvN2.getText().toString());

                    EditText tvN3;
                    tvN3 = (EditText) findViewById(R.id.EditTextN4);
                    Log.d("tvN0", tvN3.getText().toString());
                    saveTitleNum0(context, R.id.EditTextN4, tvN3.getText().toString());

                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    MainAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();

                }
            };

    View.OnClickListener mOnClickWListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MainAppWidgetConfigureActivity.this;

            RadioButton rbw = (RadioButton)findViewById(R.id.radioButtonRec);
            RadioButton rbf = (RadioButton)findViewById(R.id.radioButtonRound);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);

            if (!rbw.isChecked())
            {
                rbw.setChecked(false);
                rbf.setChecked(true);

            }
            else
            {

                rbw.setChecked(true);
                rbf.setChecked(false);
                saveBackGroundPref(context, R.id.appwidget_text, R.drawable.layout_bg);
             }

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MainAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);



        }
    };

    View.OnClickListener mOnClickFListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MainAppWidgetConfigureActivity.this;

            RadioButton rbw = (RadioButton)findViewById(R.id.radioButtonRec);
            RadioButton rbf = (RadioButton)findViewById(R.id.radioButtonRound);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
            if (!rbf.isChecked())
            {

                rbf.setChecked(false);
                rbw.setChecked(true);

            }
            else
            {
                rbf.setChecked(true);
                rbw.setChecked(false);
                saveBackGroundPref(context, R.id.appwidget_text, R.drawable.layout_bg);
             }
            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MainAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);


        }
    };

    public MainAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.main_app_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        findViewById(R.id.radioButtonRec).setOnClickListener(mOnClickWListener);
        findViewById(R.id.radioButtonRound).setOnClickListener(mOnClickFListener);

       // Intent i0 = new Intent();
      //  i0.setPackage("com.vitlem.click2call.MyService");
      //  startService(i0);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        RadioButton rbw = (RadioButton)findViewById(R.id.radioButtonRec);
        RadioButton rbf = (RadioButton)findViewById(R.id.radioButtonRound);

        int draw = MainAppWidgetConfigureActivity.loadBackGroundPref(MainAppWidgetConfigureActivity.this, R.id.appwidget_text);
        if (draw==R.drawable.layout_bg ) {
            rbf.setChecked(true);
            rbw.setChecked(false);
        }
        else {
            rbw.setChecked(true);
            rbf.setChecked(false);
        }

        mAppWidgetText.setText(loadTitlePref(MainAppWidgetConfigureActivity.this, mAppWidgetId));
        EditText et = (EditText) findViewById(R.id.EditTextN1);
        et.setText(loadTitlePrefNum0(MainAppWidgetConfigureActivity.this, R.id.EditTextN1));

        EditText et1 = (EditText) findViewById(R.id.EditTextN2);
        et1.setText(loadTitlePrefNum0(MainAppWidgetConfigureActivity.this, R.id.EditTextN2));

        EditText et2 = (EditText) findViewById(R.id.EditTextN3);
        et2.setText(loadTitlePrefNum0(MainAppWidgetConfigureActivity.this, R.id.EditTextN3));

        EditText et3 = (EditText) findViewById(R.id.EditTextN4);
        et3.setText(loadTitlePrefNum0(MainAppWidgetConfigureActivity.this, R.id.EditTextN4));

        Intent i = new Intent(this, MyService.class);
        startService (i);
        Log.d("Intent","startService");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSIONS_REQUEST_CALL_PHONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitleNum0(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePrefNum0(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            Log.d("titleValue",titleValue);
            return titleValue;
        } else {
            return "0";
        }
    }

    static void deleteTitlePrefNum0(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }


    // Write the prefix to the SharedPreferences object for this widget
    static void saveBackGroundPref(Context context, int appWidgetId, int background) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, background);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadBackGroundPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int titleValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, R.drawable.layout_bg);
        return titleValue;
    }

    static void deleteBackGroundPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

}

