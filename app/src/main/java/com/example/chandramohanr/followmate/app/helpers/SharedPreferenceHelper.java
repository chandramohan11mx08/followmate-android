package com.example.chandramohanr.followmate.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chandramohanr.followmate.app.FollowmateApplication;
import com.noveogroup.android.log.Log;

public class SharedPreferenceHelper {
    public static final String FOLLOWMATE_SHARED_PREF = "com.follomate.app";

    public static final String KEY_USER_ID = "uid";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static void openSharedPreferencesInEditMode() {
        editor = FollowmateApplication.getContext().
                getSharedPreferences(FOLLOWMATE_SHARED_PREF, Context.MODE_PRIVATE).edit();
    }

    private static void openSharedPreferencesInReadMode() {

        Context context = FollowmateApplication.getContext();
        if(context == null){
            Log.a("context is null");
        }else{
            sharedPreferences = context.
                    getSharedPreferences(FOLLOWMATE_SHARED_PREF, Context.MODE_PRIVATE);
        }
    }

    private static void closeSharedPreferences() {
        editor.commit();
    }

    public static void set(String key, String value) {
        openSharedPreferencesInEditMode();
        editor.putString(key, value);
        closeSharedPreferences();
    }

    public static String getString(String key) {
        openSharedPreferencesInReadMode();
        return sharedPreferences.getString(key, "");
    }

    public static void set(String key, int value) {
        openSharedPreferencesInEditMode();
        editor.putInt(key, value);
        closeSharedPreferences();
    }

    public static void set(String key, long value) {
        openSharedPreferencesInEditMode();
        editor.putLong(key, value);
        closeSharedPreferences();
    }

    public static void set(String key, boolean value) {
        openSharedPreferencesInEditMode();
        editor.putBoolean(key, value);
        closeSharedPreferences();
    }

    public static boolean getBoolean(String key) {
        openSharedPreferencesInReadMode();
        return sharedPreferences.getBoolean(key, false);
    }

    public static void clearSharedPreferencesData() {
        openSharedPreferencesInEditMode();
        editor.clear();
        closeSharedPreferences();
    }

    public static void deleteSharedPreference(String key) {
        openSharedPreferencesInEditMode();
        editor.remove(key);
        closeSharedPreferences();
    }
}
