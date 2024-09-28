package com.orion.messenger.ui.chat;

import android.media.VolumeShaper;

import androidx.annotation.NonNull;

public class StatusRequest {

    public int Operation;
    public int Status;
    public String Message;

    public StatusRequest() {
        Operation = 0;
        Status = 0;
        Message = "";
    }

    public StatusRequest(int operation) {
        Operation = operation;
        Status = 0;
        Message = "";
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"Operation\":"+Operation+",\"Status\":"+Status+",\"Message\":\""+Message+"\"}";
    }
}
