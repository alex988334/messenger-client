package com.orion.messenger.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    public MutableLiveData<ResponseServer> responseServer;
    public MutableLiveData<Integer> userId;
    public MutableLiveData<Integer> currentChat;

    public ChatViewModel(){
        responseServer = new MutableLiveData<>();
    }

    public void setResponseServer(ResponseServer responseServer){
        this.responseServer.setValue(responseServer);
    }
    public void setCurrentChat(int chatId){
        this.currentChat.setValue(chatId);
    }
    public void setUserId(int userId){
        this.userId.setValue(userId);
    }

    public ResponseServer getResponseServer(){
        return responseServer.getValue();
    }
}