package com.orion.messenger.ui.message;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orion.messenger.ui.chat.ResponseServer;

public class MessageViewModel extends ViewModel {

    public MutableLiveData<ResponseServer> responseServer;
    private MutableLiveData<Integer> userId;
    private MutableLiveData<Integer> currentChat;
    private MutableLiveData<Integer> currentMessageInd;
    private MutableLiveData<Long> currentMessage;
  //  private MutableLiveData<Long> selectionMessageId;

    public MessageViewModel(){

        responseServer = new MutableLiveData<>();
        userId = new MutableLiveData<>();
        currentMessageInd = new MutableLiveData<>();
        currentMessage = new MutableLiveData<>(new Long(0));
        currentChat = new MutableLiveData<>();
  //      selectionMessageId = new MutableLiveData<>();
    }

    public void setResponseServer(ResponseServer responseServer){
        this.responseServer.setValue(responseServer);
    }
    public void setCurrentMessageInd(int current){
        currentMessageInd.setValue(current);
    }
    public Integer getCurrentMessageInd(){
        return currentMessageInd.getValue();
    }
    public void setCurrentMessage(long current){
        currentMessage.setValue(current);
    }
    public Long getCurrentMessage(){
        return currentMessage.getValue();
    }
   /* public void setSelectionMessageId(long current){
        currentMessage.setValue(current);
    }
    public Long getSelectionMessageId(){
        return currentMessage.getValue();
    }*/
    public void setCurrentChat(int chatId){
        this.currentChat.setValue(chatId);
    }
    public void setUserId(int userId){
        this.userId.setValue(userId);
    }
    public Integer getCurrentChat(){
        return currentChat.getValue();
    }
    public ResponseServer getResponseServer(){
        return responseServer.getValue();
    }

}