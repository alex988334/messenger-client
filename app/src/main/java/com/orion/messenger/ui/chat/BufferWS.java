package com.orion.messenger.ui.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BufferWS implements IBufferWS{

    private List<ResponseServer> incomingData;                                                         //  тег для отладки в логах
    private List<Map<String, byte[]>> sendData;

    private List<File> sendFile;

    private boolean isAvailableIncomingData;
    private boolean isAvailableSendData;

    private ChatHandler chatHandler;

    public BufferWS(ChatHandler chatHandler){
        isAvailableIncomingData = true;
        isAvailableSendData = true;
        sendData = new ArrayList<>();
        incomingData = new ArrayList<>();
        this.chatHandler = chatHandler;
        this.sendFile = new ArrayList<>();
    }

    @Override
    public synchronized boolean haveSendMessage() {

        while (!isAvailableSendData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableSendData = false;
        boolean flag = !sendData.isEmpty();
        isAvailableSendData = true;
        notifyAll();
        return flag;
    }

    @Override
    public synchronized boolean haveResponseData() {

        while (!isAvailableIncomingData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableIncomingData = false;
        boolean flag = !incomingData.isEmpty();
        isAvailableIncomingData = true;
        notifyAll();
        return flag;
    }

    @Override
    public synchronized void addSendMessage(Map<String, byte[]> data) {

        while (!isAvailableSendData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableSendData = false;
        sendData.add(data);
        isAvailableSendData = true;
        notifyAll();
    }

    public synchronized File getSendFile() {

        if (sendFile.size() > 0) {
            File f = sendFile.get(0);
            sendFile.remove(0);
            return f;
        }
        return null;
    }

    @Override
    public synchronized void addResponseData(ResponseServer responseServer) {

        while (!isAvailableIncomingData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableIncomingData = false;
        incomingData.add(responseServer);
        isAvailableIncomingData = true;
        notifyAll();

        chatHandler.newData();
    }

    @Override
    public synchronized ResponseServer getResponseData() {

        while (!isAvailableIncomingData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableIncomingData = false;

        ResponseServer responseServer = null;
        if (incomingData.size() > 0) {
            responseServer = incomingData.get(0);
            incomingData.remove(0);
        }

        isAvailableIncomingData = true;
        notifyAll();
        return responseServer;
    }

    @Override
    public synchronized Map<String, byte[]> getSendMessage() {

        while (!isAvailableSendData) {
            try {
                wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        isAvailableSendData = false;

        Map<String, byte[]> data = null;
        if (sendData.size() > 0) {
            data = sendData.get(0);
            sendData.remove(0);
        }

        isAvailableSendData = true;
        notifyAll();
        return data;
    }
}
