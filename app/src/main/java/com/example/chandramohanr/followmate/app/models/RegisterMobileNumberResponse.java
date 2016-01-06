package com.example.chandramohanr.followmate.app.models;

import com.google.gson.annotations.SerializedName;

public class RegisterMobileNumberResponse {
    public boolean status;
    @SerializedName("is_user_created")
    public boolean is_user_created;
    @SerializedName("msg")
    public String message;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("mobile_number")
    public String mobileNumber;
    @SerializedName("isVerificationRequired")
    public boolean isVerificationRequired;
    public String code;
}
