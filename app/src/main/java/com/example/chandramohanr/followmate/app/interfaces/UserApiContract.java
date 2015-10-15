package com.example.chandramohanr.followmate.app.interfaces;

import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;

import retrofit.Callback;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface UserApiContract {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json; charset=UTF-8",
            "Referrer: http://app.followmate.com/",
    })
    @POST("/user/register")
    public void registerMobileNumber(String mobileNumber, String deviceId, Callback<RegisterMobileNumberResponse> registerMobileNumberResponseCallback);
}
