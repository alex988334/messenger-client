package com.orion.messenger.ui.chat;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {

    public MutableLiveData<ResponseServer> responseServer;
    private MutableLiveData<Integer> userId;
    private MutableLiveData<Integer> currentChat;
    private MutableLiveData<Integer> currentChatInd;
    private MutableLiveData<String> currentChatName;

    public ChatViewModel(){
        responseServer = new MutableLiveData<>(new ResponseServer());
        currentChat = new MutableLiveData<>();
        userId = new MutableLiveData<>();
        currentChatName = new MutableLiveData<>();
        currentChatInd = new MutableLiveData<>();
    }

    public void setCurrentChatInd(int chatId){
        this.currentChatInd.setValue(chatId);
    }
    public Integer getCurrentChatInd(){
        return this.currentChatInd.getValue();
    }
    public void setResponseServer(ResponseServer responseServer){
        this.responseServer.setValue(responseServer);
    }
    public void setCurrentChat(int chatId){
        this.currentChat.setValue(chatId);
    }
    public Integer getCurrentChat(){
        return (this.currentChat.getValue() != null) ? this.currentChat.getValue() : null;
    }
    public void setCurrentChatName(String chatName){
        this.currentChatName.setValue(chatName);
    }
    public String getCurrentChatName(){
        return this.currentChatName.getValue();
    }
    public void setUserId(int userId){
        this.userId.setValue(userId);
    }
    public Integer gstUserId(){
        return this.userId.getValue();
    }


    public ResponseServer getResponseServer(){
        return responseServer.getValue();
    }
}