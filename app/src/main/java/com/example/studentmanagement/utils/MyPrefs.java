package com.example.studentmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefs {
    private static final String myPref = "MyPrefs";
    private static MyPrefs instance;

    public static synchronized MyPrefs getInstance() {
        if (instance == null) {
            instance = new MyPrefs();
        }
        return instance;
    }

    public String getString(Context context, String key, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
    public void putString(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
