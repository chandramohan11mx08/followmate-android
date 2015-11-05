package com.example.chandramohanr.followmate.app.models;

import com.google.gson.annotations.SerializedName;

public class ParticipantInfo {
    @SerializedName("user_id")
    public String userId;
    @SerializedName("lastest_location")
    public UserLocation userLocation;
}
