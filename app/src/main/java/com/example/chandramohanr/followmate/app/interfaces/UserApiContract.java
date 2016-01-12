package com.example.chandramohanr.followmate.app.interfaces;

import com.example.chandramohanr.followmate.app.models.RegisterMobileNumberResponse;
import com.example.chandramohanr.followmate.app.models.SyncContactRequestBody;
import com.example.chandramohanr.followmate.app.models.events.response.DropUserFromSessionResponse;
import com.example.chandramohanr.followmate.app.models.events.response.SyncContactResponse;

import retrofit.Call;
import retrofit.http.Body;
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

    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded",
            "Referrer: http://app.followmate.com/",
    })
    @FormUrlEncoded
    @POST("/session/drop")
    public Call<DropUserFromSessionResponse> dropFromSession(@Field("session_id") String sessionId, @Field("user_id") String userId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Referrer: http://app.followmate.com/",
    })
    @POST("/user/contacts")
    public Call<SyncContactResponse> syncContacts(@Body SyncContactRequestBody syncContactRequestBody);
}
