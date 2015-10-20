package com.example.chandramohanr.followmate.app;

import com.example.chandramohanr.followmate.app.Constants.SessionContext;
import com.example.chandramohanr.followmate.app.Constants.UrlConstants;
import com.example.chandramohanr.followmate.app.controller.SessionEvents;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.example.chandramohanr.followmate.app.models.events.StartSessionRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.noveogroup.android.log.Log;

import java.net.URISyntaxException;

public class SocketController {
    private Socket mSocket;

    public void initSession(){
        if (mSocket != null && mSocket.connected()) {
            return;
        }

        try {
            mSocket = IO.socket(UrlConstants.getServerSocketUrl());
            mSocket.on(Socket.EVENT_CONNECT, onSocketConnectionSuccessful);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on("session_started", new SessionEvents().onSessionStarted);
            mSocket.connect();

        } catch (URISyntaxException e) {
            Log.a("Failed to get socket object");
        }
    }

    public Emitter.Listener onSocketConnectionSuccessful = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (!AppUtil.isAnySessionActive()) {
                Log.a("socket connected");
                StartSessionRequest startSessionRequest = new StartSessionRequest();
                startSessionRequest.userId = AppUtil.getLoggedInUserId();
                emitEvent(mSocket, "start_session", new Gson().toJson(startSessionRequest));
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.a("socket connection failed");
        }
    };

    public static boolean emitEvent(Socket socket, String eventType, Object eventObject) {
        if (eventObject == null) {
            return false;
        }

        if (socket != null && socket.connected()) {
            socket.emit(eventType, eventObject);
            return true;
        } else {
            return false;
        }
    }
}
