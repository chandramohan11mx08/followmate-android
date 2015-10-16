package com.example.chandramohanr.followmate.app.interfaces;

import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface UserApiContract {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
            "Referrer: http://app.followmate.com/",
    })
    @FormUrlEncoded
    @POST("/user/register")
    public Call<RegisterMobileNumberResponse> registerMobileNumber(@Field("mobile_number")String mobileNumber, @Field("device_id")String deviceId);
}
