package com.vitlem.findmybeacon;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import static android.util.Log.d;

/**
 * Created by NirV on 12/07/2016.
 */

public class SaveReadFromLocal {

    private static final String PREFS_NAME = "com.vitlem.findmybeacon";

    // Write the prefix to the SharedPreferences object for this widget

    static void saveBeaconinfo(Context context,String PREF_PREFIX_KEY, String Name) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY , Name);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadBeaconinfo(Context context,String PREF_PREFIX_KEY) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String Name = prefs.getString(PREF_PREFIX_KEY , null);
        if (Name != null) {
            d("loadBeaconinfo",Name);
            return Name;
        } else {
            return "0";
        }
    }

    static Hashtable<String,String> LoadAll(Context context){
        Hashtable<String,String> HTemp= new Hashtable<String,String>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Map<String, ?> allPrefs = prefs.getAll(); //your sharedPreference
        Set<String> set = allPrefs.keySet();
        int index = 0;
        for(String s : set){
            HTemp.put(s  , allPrefs.get(s).toString()+ ";" + index);
            index++;
        }
        return  HTemp;
    }

    static void deleteBeaconinfo(Context context,String PREF_PREFIX_KEY) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY);
        prefs.apply();
    }


}
