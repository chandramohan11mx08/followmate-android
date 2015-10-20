package com.example.chandramohanr.followmate.app;

import android.app.Application;
import android.content.Context;

import com.example.chandramohanr.followmate.BuildConfig;
import com.example.chandramohanr.followmate.app.Constants.SessionContext;
import com.facebook.stetho.Stetho;
import com.noveogroup.android.log.Log;

public class FollowmateApplication extends Application{

    private static Context mContext;

    public static SessionContext sessionContext;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
        if(BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this);
        }
    }

    public static Context getContext(){
        return mContext;
    }
}
