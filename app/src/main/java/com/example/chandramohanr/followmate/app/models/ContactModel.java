package com.example.chandramohanr.followmate.app.models;

import org.parceler.Parcel;

@Parcel
public class ContactModel {
    public String id;
    public String displayName;
    public String number;

    public ContactModel(){}

    public ContactModel(String id, String displayName, String number) {
        this.id = id;
        this.displayName = displayName;
        this.number = number;
    }
}
