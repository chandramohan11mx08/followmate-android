package com.example.chandramohanr.followmate.app.models.events;

import com.example.chandramohanr.followmate.app.models.UserLocation;

public class ShareLocationInfo {
    public String user_id;
    public String session_id;
    public UserLocation userLocation;

    public ShareLocationInfo(String user_id, String session_id, UserLocation userLocation) {
        this.user_id = user_id;
        this.session_id = session_id;
        this.userLocation = userLocation;
    }
}
