package com.example.chandramohanr.followmate.app.helpers;

import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.google.gson.Gson;

public class JsonParserHelper {

    public static SessionStartedEvent getSessionStartedEvent(Object arg) {
        return new Gson().fromJson(arg.toString(), SessionStartedEvent.class);
    }
}
