package com.example.chandramohanr.followmate.app.controller;

import com.example.chandramohanr.followmate.app.Constants.SessionContext;
import com.example.chandramohanr.followmate.app.FollowmateApplication;
import com.example.chandramohanr.followmate.app.helpers.JsonParserHelper;
import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.noveogroup.android.log.Log;

public class SessionEvents {

    public Emitter.Listener onSessionStarted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            SessionStartedEvent sessionStartedEvent = JsonParserHelper.getSessionStartedEvent(args[0]);
            Log.a(new Gson().toJson(sessionStartedEvent));
            if (sessionStartedEvent.is_session_created) {
                FollowmateApplication.sessionContext = new SessionContext();
                FollowmateApplication.sessionContext.isSessionActive = sessionStartedEvent.is_session_created;
            }
        }
    };
}
