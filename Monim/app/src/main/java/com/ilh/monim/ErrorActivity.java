package com.ilh.monim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    static  String Udid="yy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView tv = (TextView)findViewById(R.id.ErrtextView);
        tv.setText(Udid + " מכשיר לא מזוהה ");
    }
}
