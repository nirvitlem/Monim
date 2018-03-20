package com.vitlem.findmybeacon;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import static com.vitlem.findmybeacon.MainActivity.context;
import static com.vitlem.findmybeacon.R.id.ListA;

public class MangeBeacon extends AppCompatActivity {
    private static ListView lv;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    private static Hashtable<String,String> hashReg  = new Hashtable<String,String>();
    private String itemValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mange_beacon);

        lv = (ListView) findViewById(ListA);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        lv.setAdapter(adapter);
        hashReg = SaveReadFromLocal.LoadAll(context);

        for(Map.Entry entry: hashReg.entrySet()){
            entry.hashCode();
            adapter.add(entry.getValue() + "_" + entry.getKey());
            }


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {

            view.setSelected(true);
            // ListView Clicked item index
            int itemPosition = position;
            // ListView Clicked item value
            itemValue = (String) lv.getItemAtPosition(position);

        }

    });


        final Button DelButton = (Button) findViewById(R.id.DelB);
        DelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MangeBeacon.this);

                builder.setTitle("Do this action");
                builder.setMessage("do you want confirm this action? " + itemValue.split("_")[1].toString()+ "_" +itemValue.split("_")[2].toString());

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do do my action here
                        SaveReadFromLocal.deleteBeaconinfo(context,itemValue.split("_")[1].toString()+ "_" +itemValue.split("_")[2].toString() );
                        dialog.dismiss();
                        hashReg = SaveReadFromLocal.LoadAll(context);
                        listItems.clear();
                        adapter.notifyDataSetChanged();
                        for(Map.Entry entry: hashReg.entrySet()){
                            entry.hashCode();
                            adapter.add(entry.getValue() + "_" + entry.getKey());
                        }

                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // I do not need any action here you might
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }
}
