package com.example.chandramohanr.followmate.app.models.events;

import com.example.chandramohanr.followmate.app.models.UserLocation;
import com.google.gson.annotations.SerializedName;

public class StartSessionRequest {
    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_location")
    public UserLocation userLocation;

    public StartSessionRequest(String userId, UserLocation userLocation) {
        this.userId = userId;
        this.userLocation = userLocation;
    }
}
