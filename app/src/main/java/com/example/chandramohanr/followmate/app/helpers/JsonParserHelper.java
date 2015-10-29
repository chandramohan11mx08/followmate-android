package com.example.chandramohanr.followmate.app.helpers;

import com.example.chandramohanr.followmate.app.models.events.response.JoinRoomResponse;
import com.example.chandramohanr.followmate.app.models.events.response.NewUserJoinedEvent;
import com.example.chandramohanr.followmate.app.models.events.response.ReconnectedToSession;
import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.google.gson.Gson;

public class JsonParserHelper {

    public static SessionStartedEvent getSessionStartedEvent(Object arg) {
        return new Gson().fromJson(arg.toString(), SessionStartedEvent.class);
    }

    public static JoinRoomResponse getJoinSessionResponseEvent(Object arg) {
        return new Gson().fromJson(arg.toString(), JoinRoomResponse.class);
    }

    public static NewUserJoinedEvent getNewUserJoinedSessionResponseEvent(Object arg) {
        return new Gson().fromJson(arg.toString(), NewUserJoinedEvent.class);
    }

    public static ReconnectedToSession getRejoinedSessionResponseEvent(Object arg) {
        return new Gson().fromJson(arg.toString(), ReconnectedToSession.class);
    }
}
