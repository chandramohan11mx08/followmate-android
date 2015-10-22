package com.example.chandramohanr.followmate.app.controller;

import com.example.chandramohanr.followmate.app.Constants.SessionContext;
import com.example.chandramohanr.followmate.app.FollowmateApplication;
import com.example.chandramohanr.followmate.app.helpers.JsonParserHelper;
import com.example.chandramohanr.followmate.app.models.events.response.JoinRoomResponse;
import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.github.nkzawa.emitter.Emitter;

import de.greenrobot.event.EventBus;

public class SessionEvents {

    final EventBus eventBus = new EventBus();

    public Emitter.Listener onSessionStarted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            SessionStartedEvent sessionStartedEvent = JsonParserHelper.getSessionStartedEvent(args[0]);
            if (sessionStartedEvent.is_session_created) {
                FollowmateApplication.sessionContext = new SessionContext();
                FollowmateApplication.sessionContext.isSessionActive = sessionStartedEvent.is_session_created;
            }
            eventBus.post(sessionStartedEvent);
        }
    };

    public Emitter.Listener onJoinedSession = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JoinRoomResponse joinRoomResponse = JsonParserHelper.getJoinSessionResponseEvent(args[0]);
            eventBus.post(joinRoomResponse);
        }
    };
}
