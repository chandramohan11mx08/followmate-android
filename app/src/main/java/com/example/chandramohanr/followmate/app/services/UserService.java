package com.example.chandramohanr.followmate.app.services;


import android.app.IntentService;
import android.content.Intent;

import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.Constants.UrlConstants;
import com.example.chandramohanr.followmate.app.helpers.RestServiceGenerator;
import com.example.chandramohanr.followmate.app.interfaces.UserApiContract;
import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;
import com.example.chandramohanr.followmate.app.models.events.response.DropUserFromSessionResponse;
import com.noveogroup.android.log.Log;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserService extends IntentService {

    public final static int REGISTER_USER_API = 1;
    public final static int DROP_SESSION_API = 2;
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
            case REGISTER_USER_API:
                String mobileNumber = intent.getStringExtra(AppConstants.MOBILE_NUMBER);
                String deviceId = intent.getStringExtra(AppConstants.DEVICE_ID);
                registerUser(mobileNumber, deviceId);
                break;
            case DROP_SESSION_API:
                String sessionId = intent.getStringExtra(AppConstants.SESSION_ID);
                String userId = intent.getStringExtra(AppConstants.USER_ID);
                dropFromSession(sessionId, userId);
                break;
            default:
                Log.a("No matching api type");
        }
    }

    public void registerUser(String mobileNumber, String deviceId) {
        Call<RegisterMobileNumberResponse> call = userApiContract.registerMobileNumber(mobileNumber, deviceId);
        call.enqueue(new Callback<RegisterMobileNumberResponse>() {
            @Override
            public void onResponse(Response<RegisterMobileNumberResponse> response, Retrofit retrofit) {
                Log.a("Response got ");
                RegisterMobileNumberResponse registerMobileNumberResponse = response.body();
                if(registerMobileNumberResponse!=null){
                    registerMobileNumberResponse.status = true;
                    EventBus.getDefault().post(registerMobileNumberResponse);
                }else{
                 broadcastError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.a("Response failed ");
                broadcastError();
            }

            private void broadcastError() {
                RegisterMobileNumberResponse registerMobileNumberResponse = new RegisterMobileNumberResponse();
                registerMobileNumberResponse.status = false;
                EventBus.getDefault().post(registerMobileNumberResponse);
            }
        });
    }

    public void dropFromSession(String sessionId, String userId){
        Call<DropUserFromSessionResponse> call = userApiContract.dropFromSession(sessionId, userId);
        call.enqueue(new Callback<DropUserFromSessionResponse>() {
            @Override
            public void onResponse(Response<DropUserFromSessionResponse> response, Retrofit retrofit) {
                Log.a("Response got ");
                DropUserFromSessionResponse dropUserFromSessionResponse = response.body();
                if(dropUserFromSessionResponse!=null){
                    dropUserFromSessionResponse.apiStatus = true;
                    EventBus.getDefault().post(dropUserFromSessionResponse);
                }else{
                    broadcastError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.a("Response failed ");
                broadcastError();
            }

            private void broadcastError() {
                DropUserFromSessionResponse dropUserFromSessionResponse = new DropUserFromSessionResponse();
                EventBus.getDefault().post(dropUserFromSessionResponse);
            }
        });
    }
}
