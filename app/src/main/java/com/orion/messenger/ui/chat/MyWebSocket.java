package com.orion.messenger.ui.chat;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyWebSocket extends WebSocketClient {

    private WSThread wsHandler;
    private boolean connected;

    public MyWebSocket(URI serverUri, WSThread wsHandler) {                                                         //  в параметри указан полный uri c портом
        super(serverUri);
        this.wsHandler = wsHandler;
        connected = false;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(ConstWSThread.LOG_TAG, "WebSocket -> CONNECTED!");
        wsHandler.onConnectedServer();
        connected = true;
    }

    @Override
    public void onMessage(String message) {

        Type type = new TypeToken<ResponseServer>(){}.getType();
        ResponseServer r = new Gson().fromJson(message, type);

        Log.d("", "MyWebSocket RESPONSE! "
                + StatusRequest.getDescriptionOperation(r.Status.Operation) + " " +
                StatusRequest.getDescriptionStatus(r.Status.Status)+ " -> " +  message);
        wsHandler.newResponse(r);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {                              //  отрабатывает когда соединение закрыто
        Log.d(ConstWSThread.LOG_TAG, "WebSocket -> DISCONNECTED!");
        connected = false;
    }

    @Override
    public void onError(Exception ex) {
        Log.d(ConstWSThread.LOG_TAG, "ERROR WebSocket: !"  + ex.getMessage() + ", "
                + ex.getLocalizedMessage() + ", " + ex.getStackTrace().toString());
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }
}