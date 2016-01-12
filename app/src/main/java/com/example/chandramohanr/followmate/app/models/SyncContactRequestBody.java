package com.example.chandramohanr.followmate.app.models;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class SyncContactRequestBody {
    public String user_id;
    public List<ContactModel> contacts;

    public SyncContactRequestBody(){}

    public SyncContactRequestBody(String user_id, List<ContactModel> contactModelList) {
        this.user_id = user_id;
        this.contacts = contactModelList;
    }

}
