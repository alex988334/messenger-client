package com.orion.messenger.ui.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.orion.messenger.ui.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponseServer {

    public StatusRequest Status = new StatusRequest();
    public List<Chat> Chat = new ArrayList<>();
    public List<ChatUser> ChatUser = new ArrayList<>();
    public List<Message> Message = new ArrayList<>();
    public List<MessageStatus> MessageStatus = new ArrayList<>();
    public List<User> User = new ArrayList<>();
    public List<BlackList> BlackList = new ArrayList<>();
    public List<UserPhone> UserPhone = new ArrayList<>();

    public ResponseServer(){
        Status = new StatusRequest();
        Chat = new ArrayList<>();
        ChatUser = new ArrayList<>();
        Message = new ArrayList<>();
        MessageStatus = new ArrayList<>();
        User = new ArrayList<>();
        BlackList = new ArrayList<>();
        UserPhone = new ArrayList<>();
    }

    public void appendData(ResponseServer r) {

       // Chat.addAll(r.Chat);
        for (Chat m: r.Chat) {
            if (!Chat.contains(m)) {
                Chat.add(m);
            }
        }
       // ChatUser.addAll(r.ChatUser);
        for (ChatUser m: r.ChatUser) {
            if (!ChatUser.contains(m)) {
                ChatUser.add(m);
            }
        }
       // Message.addAll(r.Message);
        for (Message m: r.Message) {
            if (!Message.contains(m)) {
                Message.add(m);
            }
        }
     //   MessageStatus.addAll(r.MessageStatus);
        for (MessageStatus m: r.MessageStatus) {
            if (!MessageStatus.contains(m)) {
                MessageStatus.add(m);
            }
        }
    //    User.addAll(r.User);
        for (User m: r.User) {
            if (!User.contains(m)) {
                User.add(m);
            }
        }
       // BlackList.addAll(r.BlackList);
        for (BlackList m: r.BlackList) {
            if (!BlackList.contains(m)) {
                BlackList.add(m);
            }
        }
    //    UserPhone.addAll(r.UserPhone);
        for (UserPhone m: r.UserPhone) {
            if (!UserPhone.contains(m)) {
                UserPhone.add(m);
            }
        }
    }

    public void prependData(ResponseServer r) {

      //  r.Chat.addAll(Chat);
        for (Chat m: Chat) {
            if (!r.Chat.contains(m)) {
                r.Chat.add(m);
            }
        }
      //  r.ChatUser.addAll(ChatUser);
        for (ChatUser m: ChatUser) {
            if (!r.ChatUser.contains(m)) {
                r.ChatUser.add(m);
            }
        }
      //  r.Message.addAll(Message);
        for (Message m: Message) {
            if (!r.Message.contains(m)) {
                r.Message.add(m);
            }
        }
     //   r.MessageStatus.addAll(MessageStatus);
        for (MessageStatus m: MessageStatus) {
            if (!r.MessageStatus.contains(m)) {
                r.MessageStatus.add(m);
            }
        }
       // r.User.addAll(User);
        for (User m: User) {
            if (!r.User.contains(m)) {
                r.User.add(m);
            }
        }
       // r.BlackList.addAll(BlackList);
        for (BlackList m: BlackList) {
            if (!r.BlackList.contains(m)) {
                r.BlackList.add(m);
            }
        }
      //  r.UserPhone.addAll(UserPhone);
        for (UserPhone m: UserPhone) {
            if (!r.UserPhone.contains(m)) {
                r.UserPhone.add(m);
            }
        }

        Chat = r.Chat;
        ChatUser = r.ChatUser;
        Message = r.Message;
        MessageStatus = r.MessageStatus;
        User = r.User;
        BlackList = r.BlackList;
        UserPhone = r.UserPhone;
    }

    public void fillCollectionForNullLinks() {

        if (Chat == null) {
            Chat = new ArrayList<>();
        }
        if (ChatUser == null) {
            ChatUser = new ArrayList<>();
        }
        if (Message == null) {
            Message = new ArrayList<>();
        }
        if (MessageStatus == null) {
            MessageStatus = new ArrayList<>();
        }
        if (User == null) {
            User = new ArrayList<>();
        }
        if (BlackList == null) {
            BlackList = new ArrayList<>();
        }
        if (UserPhone == null) {
            UserPhone = new ArrayList<>();
        }
    }

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
