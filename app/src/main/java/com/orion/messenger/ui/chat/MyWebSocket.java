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
        Log.d(ConstWSThread.LOG_TAG, "WebSocket open connection!");
        wsHandler.onConnectedServer();
        connected = true;
    }

    @Override
    public void onMessage(String message) {                                                     //  обрабатывает входящее сообщение
        Log.d(ConstWSThread.LOG_TAG, "MyWebSocket! RESPONSE FROM ORION => " + message);
        Type type = new TypeToken<ResponseServer>(){}.getType();
        wsHandler.newResponse(new Gson().fromJson(message, type));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {                              //  отрабатывает когда соединение закрыто
        Log.d(ConstWSThread.LOG_TAG, "WS СОЕДИНЕНИЕ ЗАКРЫТО");
        connected = false;
    }

    @Override
    public void onError(Exception ex) {                                                         //  отрабатывает когда происходит ошибка в работе
        Log.d(ConstWSThread.LOG_TAG, "ERROR WS 6676516: " + ex.getMessage() + ", " + ex.getLocalizedMessage() + ", " + ex.getStackTrace().toString());
        connected = false;
    }

    public boolean isConnected(){
        return connected;
    }
}