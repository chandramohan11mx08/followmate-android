package com.example.chandramohanr.followmate.app;

import android.content.Context;
import android.content.Intent;

import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.Constants.UrlConstants;
import com.example.chandramohanr.followmate.app.controller.SessionEvents;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.helpers.SharedPreferenceHelper;
import com.example.chandramohanr.followmate.app.models.events.ChangeMarkerVisibility;
import com.example.chandramohanr.followmate.app.models.events.ShareLocationInfo;
import com.example.chandramohanr.followmate.app.models.events.StartSessionRequest;
import com.example.chandramohanr.followmate.app.models.events.request.JoinSessionRequest;
import com.example.chandramohanr.followmate.app.models.events.request.TerminateSession;
import com.example.chandramohanr.followmate.app.services.UserService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.noveogroup.android.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketController {
    private Socket mSocket;
    private Context context;

    public SocketController(Context context){
        this.context = context;
    }

    public boolean initSession(){
        if (mSocket != null && mSocket.connected()) {
            return true;
        }

        try {
            mSocket = IO.socket(UrlConstants.getServerSocketUrl());
            mSocket.on(Socket.EVENT_CONNECT, onSocketConnectionSuccessful);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("session_started", new SessionEvents().onSessionStarted);
            mSocket.on("joined_session", new SessionEvents().onJoinedSession);
            mSocket.on("new_user_joined", new SessionEvents().onNewUserJoined);
            mSocket.on("user_location", new SessionEvents().onUserLocationUpdate);
            mSocket.on("rejoined", new SessionEvents().onRejoined);
            mSocket.on("visibility_changed", new SessionEvents().onUserVisibilityChanged);
            mSocket.on("terminated", new SessionEvents().onSessionTerminated);
            mSocket.on("ping", new SessionEvents().onPingReceived);
            mSocket.connect();
            return true;
        } catch (URISyntaxException e) {
            Log.a("Failed to get socket object");
            return false;
        }
    }

    public void connect(StartSessionRequest startSessionRequest) {
        String json = new Gson().toJson(startSessionRequest);
        convertToJsonAndEmit(json, "start_session");
    }

    public void shareLocation(ShareLocationInfo shareLocationInfo){
        String json = new Gson().toJson(shareLocationInfo);
        convertToJsonAndEmit(json, "share_location");
    }

    public void disconnect(){
        if(mSocket!=null){
            mSocket.disconnect();
        }
    }

    public void joinSession(JoinSessionRequest joinSessionRequest){
        String json = new Gson().toJson(joinSessionRequest);
        convertToJsonAndEmit(json, "join_session");
    }

    public void rejoinSession(JoinSessionRequest joinSessionRequest){
        String json = new Gson().toJson(joinSessionRequest);
        convertToJsonAndEmit(json, "rejoin_session");
    }

    public void changeVisibility(ChangeMarkerVisibility changeMarkerVisibility){
        String json = new Gson().toJson(changeMarkerVisibility);
        convertToJsonAndEmit(json, "change_visibility");
    }

    public void terminateSession(TerminateSession terminateSession){
        String json = new Gson().toJson(terminateSession);
        convertToJsonAndEmit(json, "end_session");
    }

    public void convertToJsonAndEmit(String json, String event){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(mSocket != null){
                emitEvent(mSocket, event, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Emitter.Listener onSocketConnectionSuccessful = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            boolean anySessionActive = AppUtil.isAnySessionActive();
            String sessionToBeConnected = SharedPreferenceHelper.getString(SharedPreferenceHelper.KEY_SESSION_TO_JOIN);
            if (anySessionActive) {
                new SessionEvents().rejoinToPreviousActiveSession();
            }
            else if(sessionToBeConnected != null && !sessionToBeConnected.isEmpty()){
                new SessionEvents().notifyJoinSessionFailure(sessionToBeConnected);
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.a("socket connection failed");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            boolean anySessionActive = AppUtil.isAnySessionActive();
            if (anySessionActive) {
                Intent intent = new Intent(context, UserService.class);
                intent.putExtra(AppConstants.SERVICE_TYPE, UserService.DROP_SESSION_API);
                intent.putExtra(AppConstants.SESSION_ID, AppUtil.getSessionId());
                intent.putExtra(AppConstants.USER_ID, AppUtil.getLoggedInUserId());
                context.startService(intent);
            }
        }
    };

    private Emitter.Listener onReconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.a("socket reconnect");
        }
    };

    public static boolean emitEvent(Socket socket, String eventType, Object eventObject) {
        if (eventObject == null) {
            return false;
        }

        if (socket != null) {
            socket.emit(eventType, eventObject);
            return true;
        } else {
            return false;
        }
    }
}
