package com.example.chandramohanr.followmate.app;

import com.example.chandramohanr.followmate.app.Constants.UrlConstants;
import com.example.chandramohanr.followmate.app.controller.SessionEvents;
import com.example.chandramohanr.followmate.app.helpers.AppUtil;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.noveogroup.android.log.Log;

import org.json.JSONObject;

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
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_RECONNECT, onReconnect);
            mSocket.on("session_started", new SessionEvents().onSessionStarted);
            mSocket.on("joined_session", new SessionEvents().onJoinedSession);
            mSocket.on("new_user_joined", new SessionEvents().onNewUserJoined);
            mSocket.on("rejoined", new SessionEvents().onRejoined);
            mSocket.connect();

        } catch (URISyntaxException e) {
            Log.a("Failed to get socket object");
        }
    }

    public void connect(JSONObject jsonObject) {
        emitEvent(mSocket, "start_session", jsonObject);
    }

    public void disconnect(){
        if(mSocket!=null){
            mSocket.disconnect();
        }
    }

    public void joinSession(JSONObject jsonObject){
        if(mSocket != null && mSocket.connected()){
            emitEvent(mSocket, "join_session", jsonObject);
        }
    }

    public void rejoinSession(JSONObject jsonObject){
        if(mSocket != null && mSocket.connected()){
            emitEvent(mSocket, "rejoin_session",jsonObject);
        }
    }

    public Emitter.Listener onSocketConnectionSuccessful = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            boolean anySessionActive = AppUtil.isAnySessionActive();
            Log.a("session active "+anySessionActive);
            if (anySessionActive) {
                new SessionEvents().rejoinToPreviousActiveSession();
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
            Log.a("socket disconnect");
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

        if (socket != null && socket.connected()) {
            socket.emit(eventType, eventObject);
            return true;
        } else {
            return false;
        }
    }
}
