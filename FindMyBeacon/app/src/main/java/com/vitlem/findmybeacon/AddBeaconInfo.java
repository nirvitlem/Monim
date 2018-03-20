package com.vitlem.findmybeacon;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddBeaconInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon_info);

        final Context context = AddBeaconInfo.this;

        final Intent intent = getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = (TextView)findViewById(R.id.textUdid) ;
        textView.setText(message);

        final Button Savebutton = (Button) findViewById(R.id.SaveB);
        Savebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.textName);
                SaveReadFromLocal.saveBeaconinfo(context,message,et.getText().toString());
                finish();
            }
        });


    }
}
