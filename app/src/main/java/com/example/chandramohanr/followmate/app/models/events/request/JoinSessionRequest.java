package com.example.chandramohanr.followmate.app.models.events.request;

import com.example.chandramohanr.followmate.app.models.UserLocation;

public class JoinSessionRequest {
    public String session_id;
    public String user_id;
    public UserLocation user_location;
    public boolean visibility = false;

    public JoinSessionRequest(){}

    public JoinSessionRequest(String session_id, String user_id, UserLocation userLocation) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.user_location = userLocation;
        visibility = false;
    }
}
