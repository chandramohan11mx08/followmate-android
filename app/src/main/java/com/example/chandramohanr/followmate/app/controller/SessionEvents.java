package com.example.chandramohanr.followmate.app.controller;

import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.helpers.JsonParserHelper;
import com.example.chandramohanr.followmate.app.models.events.ChangeMarkerVisibility;
import com.example.chandramohanr.followmate.app.models.events.SessionConnectionSocketFailure;
import com.example.chandramohanr.followmate.app.models.events.ShareLocationInfo;
import com.example.chandramohanr.followmate.app.models.events.response.JoinRoomResponse;
import com.example.chandramohanr.followmate.app.models.events.response.NewUserJoinedEvent;
import com.example.chandramohanr.followmate.app.models.events.response.ReconnectToPreviousLostSession;
import com.example.chandramohanr.followmate.app.models.events.response.ReconnectedToSession;
import com.example.chandramohanr.followmate.app.models.events.response.SessionStartedEvent;
import com.github.nkzawa.emitter.Emitter;
import com.noveogroup.android.log.Log;

import de.greenrobot.event.EventBus;

public class SessionEvents {

    final EventBus eventBus = EventBus.getDefault();

    public Emitter.Listener onSessionStarted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            SessionStartedEvent sessionStartedEvent = JsonParserHelper.getSessionStartedEvent(args[0]);
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

    public Emitter.Listener onNewUserJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            NewUserJoinedEvent newUserJoinedEvent = JsonParserHelper.getNewUserJoinedSessionResponseEvent(args[0]);
            eventBus.post(newUserJoinedEvent);
        }
    };

    public Emitter.Listener onUserLocationUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ShareLocationInfo userLocationUpdateInfo = JsonParserHelper.getUserLocationResponseEvent(args[0]);
            eventBus.post(userLocationUpdateInfo);
        }
    };

    public Emitter.Listener onRejoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ReconnectedToSession reconnectedToSession = JsonParserHelper.getRejoinedSessionResponseEvent(args[0]);
            eventBus.post(reconnectedToSession);
        }
    };

    public Emitter.Listener onUserVisibilityChanged = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChangeMarkerVisibility changeMarkerVisibilityEvent = JsonParserHelper.getChangeMarkerVisibilityResponseEvent(args[0]);
            eventBus.post(changeMarkerVisibilityEvent);
        }
    };

    public Emitter.Listener onPingReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.a("Ping received");
        }
    };

    public void rejoinToPreviousActiveSession() {
        ReconnectToPreviousLostSession reconnectToPreviousLostSession = new ReconnectToPreviousLostSession();
        reconnectToPreviousLostSession.sessionId = AppUtil.getSessionId();
        eventBus.post(reconnectToPreviousLostSession);
    }

    public void notifyJoinSessionFailure(String sessionToBeConnected){
        SessionConnectionSocketFailure sessionConnectionSocketFailure = new SessionConnectionSocketFailure(sessionToBeConnected);
        eventBus.post(sessionConnectionSocketFailure);
    }
}
