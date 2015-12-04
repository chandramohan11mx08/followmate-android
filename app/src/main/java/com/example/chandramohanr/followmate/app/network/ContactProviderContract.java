package com.example.chandramohanr.followmate.app.network;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContactProviderContract {
    private ContactProviderContract() {
    }
    public static final String CONTENT_AUTHORITY = "com.followmate.app.contacts";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String TABLE_NAME = "contacts";
    public static final String CONTACT_ID = BaseColumns._ID;
    public static final Uri CONTACTS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static final String DISPLAY_NAME = "display_name";
    public static final String MOBILE_NUMBER = "mobile_number";
}
