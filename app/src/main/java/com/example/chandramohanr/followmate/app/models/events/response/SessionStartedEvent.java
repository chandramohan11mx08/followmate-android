package com.example.chandramohanr.followmate.app.models.events.response;

public class SessionStartedEvent {
    public boolean is_session_created;
    public String session_id;
    public String msg;

    public SessionStartedEvent(boolean is_session_created, String msg){
        this.is_session_created = is_session_created;
        this.msg = msg;
    }
}
