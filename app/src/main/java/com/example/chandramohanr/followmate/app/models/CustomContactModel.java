package com.example.chandramohanr.followmate.app.models;

import com.google.gson.annotations.SerializedName;

public class CustomContactModel {
    @SerializedName("user_id")
    public String userId;
    @SerializedName("mobile_number")
    public String mobileNumber;
    public String displayName;
}
