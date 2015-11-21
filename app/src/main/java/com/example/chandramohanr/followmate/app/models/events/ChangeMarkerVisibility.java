package com.example.chandramohanr.followmate.app.models.events;

public class ChangeMarkerVisibility {
    public String session_id;
    public String user_id;
    public boolean visibility;

    public ChangeMarkerVisibility(String session_id, String user_id, boolean visible) {
        this.session_id = session_id;
        this.user_id = user_id;
        this.visibility = visible;
    }
}
