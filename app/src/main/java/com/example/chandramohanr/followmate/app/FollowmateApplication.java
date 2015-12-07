package com.example.chandramohanr.followmate.app;

import android.app.Application;
import android.content.Context;

import com.example.chandramohanr.followmate.BuildConfig;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
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
        AppUtil.resetSessionInfo();
//        Intent intent = new Intent(mContext, ContactService.class);
//        startService(intent);
    }

    public static Context getContext(){
        return mContext;
    }
}
