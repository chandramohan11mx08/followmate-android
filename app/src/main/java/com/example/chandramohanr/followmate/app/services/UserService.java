package com.example.chandramohanr.followmate.app.services;


import android.app.IntentService;
import android.content.Intent;

import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.noveogroup.android.log.Log;

public class UserService extends IntentService {

    public UserService(){
        super("UserService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.a("User service started");
        int apiType = intent.getIntExtra(AppConstants.SERVICE_TYPE,0);
        switch (apiType){
            case 1:
                Log.a("User register api call");
                break;
            default:
                Log.a("No matching api type");
        }
    }
}
