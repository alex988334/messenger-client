package com.orion.messenger.ui.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.orion.messenger.ui.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseServer {

    public static final int OP_AUTORIZATE             = 100;
    public static final int OP_STATUS_MESSAGE         = 101;
    public static final int OP_NEW_MESSAGE            = 103;
    public static final int OP_LIST_USERS             = 105;
    public static final int OP_CREATE_NEW_CHAT        = 106;
    public static final int OP_WRITEN                 = 107;
    public static final int OP_SYSTEM                 = 108;
    public static final int OP_SEARCH_USER            = 109;
    public static final int OP_GET_CHATS              = 110;
    public static final int OP_LIST_PREVIOUS_MESSAGES = 111;
    public static final int OP_LIST_NEXT_MESSAGES     = 112;
    public static final int OP_EXIT_CHAT              = 113;
    public static final int OP_REMOVE_USER            = 114;
    public static final int OP_ADD_USER               = 115;
    public static final int OP_REMOVE_CHAT            = 116;
    public static final int OP_BLOCK_USERS            = 117;
    public static final int OP_UNLOOCK_USERS          = 118;
    public static final int OP_BLACK_LIST_USERS       = 119;
    public static final int OP_GET_FILE               = 120;
    public static final int OP_MY_DATA                = 122;
    public static final int OP_REGISTRATION           = 123;

    public StatusRequest Status;
    public Chat[] Chat;
    public ChatUser[] ChatUser;
    public Message[] Message;
    public MessageStatus[] MessageStatus;
    public User[] User;
    public BlackList[] BlackList;
    public UserPhone[] UserPhone;

   /* public static ResponseServer mergeResponses(ResponseServer r1, ResponseServer r2){

        ResponseServer rez = new ResponseServer();
        int total = 0;
        if (r1.Chat != null)
        rez.Chat = new Chat[total];


    }*/

    public ResponseServer(){
      /*  Status = new StatusRequest();
        Chat = new Chat[0];
        ChatUser = new ChatUser[0];
        Message = new Message[0];
        MessageStatus = new MessageStatus[0];
        User = new User[0];
        BlackList = new BlackList[0];
        UserPhone = new UserPhone[0];*/
    }

   /* public ResponseServer(StatusRequest model, Map<String, List<IModel>> data){
        this.status = model;
        this.data = data;
        if (!data.isEmpty()) {
            empty = false;
        }
    }*/

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
/*
    public ResponseServer(StatusRequest model) {
        this.status = model;
        this.data = new HashMap<String,List<IModel>>();
    }

    public int getStatusResponse() {
        return status.Status;
    }

    public int getOperation() {
        return status.Operation;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setData(Map<String, List<IModel>> data){
        this.data = data;
        if (!data.isEmpty()) {
            empty = false;
        }
    }

    public void appendModel(IModel model) {

        String mName = model.NameModel();
        List<IModel> models;

        if (data.containsKey(mName)) {
            models = data.get(mName);
        } else {
            models = new ArrayList<>();
            data.put(mName, models);
        }

        models.add(model);
        empty = false;
    }

    public void appendModels(List<IModel> models) {

        if (models.isEmpty()) {
            Log.e(ConstWSThread.LOG_TAG, "ResponseData ERROR! Append models list is empty!");
            return;
        }

        for(IModel model: models) {
            appendModel(model);
        }
    }

    public void setModels(List<IModel> models) {

        if (models.isEmpty()) {
            Log.e(ConstWSThread.LOG_TAG, "ResponseData ERROR! Append models list is empty!");
            return;
        }

        data.put(models.get(0).NameModel(), models);
        if (!models.isEmpty()) {
            empty = false;
        }
    }

    public List<IModel> getModels(String modelName){

        List<IModel> d;

        if (data.containsKey(modelName)) {
            d = data.get(modelName);
        } else {
            d = new ArrayList<IModel>();
        }

        return d;
    }*/
}
