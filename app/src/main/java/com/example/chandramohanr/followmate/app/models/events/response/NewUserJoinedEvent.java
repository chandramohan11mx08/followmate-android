package com.example.chandramohanr.followmate.app.models.events.response;

import com.example.chandramohanr.followmate.app.models.UserLocation;

public class NewUserJoinedEvent {
    public String user_id;
    public UserLocation userLocation = new UserLocation(12.9667, 77.5667);
}
