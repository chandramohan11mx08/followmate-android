package com.example.chandramohanr.followmate.app.models.events.response;

import com.example.chandramohanr.followmate.app.models.CustomContactModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncContactResponse {
    public boolean status;
    @SerializedName("contacts")
    public List<CustomContactModel> customContactModelList;
}
