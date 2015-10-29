package com.example.chandramohanr.followmate.app;

import android.app.Application;
import android.content.Context;

import com.example.chandramohanr.followmate.BuildConfig;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.facebook.stetho.Stetho;

public class FollowmateApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_IS_SESSION_ACTIVE);
        SharedPreferenceHelper.deleteSharedPreference(SharedPreferenceHelper.KEY_ACTIVE_SESSION_ID);
    }

    public static Context getContext(){
        return mContext;
    }
}
