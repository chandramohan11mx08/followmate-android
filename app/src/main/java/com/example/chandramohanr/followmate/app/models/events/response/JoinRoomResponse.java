package com.example.chandramohanr.followmate.app.models.events.response;

import com.example.chandramohanr.followmate.app.models.ParticipantInfo;

import java.util.List;

public class JoinRoomResponse {
    public boolean joined;
    public String session_id;
    public List<ParticipantInfo> participants;
}
