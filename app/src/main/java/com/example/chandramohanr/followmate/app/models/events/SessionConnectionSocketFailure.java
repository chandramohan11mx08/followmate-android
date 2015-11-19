package com.example.chandramohanr.followmate.app.models.events;

public class SessionConnectionSocketFailure {
    public String sessionId;
    public SessionConnectionSocketFailure(String sessionId){
        this.sessionId = sessionId;
    }
}
