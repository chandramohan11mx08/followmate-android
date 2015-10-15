package com.example.chandramohanr.followmate.app;

import android.app.Application;
import android.content.Context;

import com.noveogroup.android.log.Log;

public class FollowmateApplication extends Application{

    private static Context mContext;


    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
        Log.a("Application started");
    }

    public static Context getContext(){
        return mContext;
    }
}
