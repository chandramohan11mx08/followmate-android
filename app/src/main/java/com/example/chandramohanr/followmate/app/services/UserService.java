package com.example.chandramohanr.followmate.app.services;


import android.app.IntentService;
import android.content.Intent;

import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.Constants.UrlConstants;
import com.example.chandramohanr.followmate.app.helpers.RestServiceGenerator;
import com.example.chandramohanr.followmate.app.interfaces.UserApiContract;
import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;
import com.noveogroup.android.log.Log;

import de.greenrobot.event.EventBus;
import retrofit.Response;
import retrofit.Retrofit;

public class UserService extends IntentService {

    public UserApiContract userApiContract = null;

    public UserService() {
        super("UserService");
        userApiContract = RestServiceGenerator.createRestApiServiceGenerator(UserApiContract.class, UrlConstants.getUserRegisterUrl());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.a("User service started");
        int apiType = intent.getIntExtra(AppConstants.SERVICE_TYPE, 0);
        switch (apiType) {
            case 1:
                Log.a("User register api call");
                break;
            default:
                Log.a("No matching api type");
        }
    }

    public void registerUser(String mobileNumber, String deviceId) {
        userApiContract.registerMobileNumber(mobileNumber, deviceId, new retrofit.Callback<RegisterMobileNumberResponse>() {
            @Override
            public void onResponse(Response<RegisterMobileNumberResponse> response, Retrofit retrofit) {
                RegisterMobileNumberResponse registerMobileNumberResponse = response.body();
                registerMobileNumberResponse.status = true;
                EventBus.getDefault().post(registerMobileNumberResponse);
            }

            @Override
            public void onFailure(Throwable t) {
                RegisterMobileNumberResponse registerMobileNumberResponse = new RegisterMobileNumberResponse();
                registerMobileNumberResponse.status = false;
                EventBus.getDefault().post(registerMobileNumberResponse);
            }
        });
    }
}
