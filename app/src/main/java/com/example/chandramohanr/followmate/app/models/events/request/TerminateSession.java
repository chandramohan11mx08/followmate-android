package com.example.chandramohanr.followmate.app.models.events.request;

public class TerminateSession {
    public String session_id;
    public String user_id;

    public TerminateSession(String sessionId, String userId) {
        this.session_id = sessionId;
        this.user_id = userId;
    }
}
