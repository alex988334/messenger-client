package com.orion.messenger.ui.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orion.messenger.ui.chat.ResponseServer;

public class ListUsersViewModel extends ViewModel {

    public MutableLiveData<ResponseServer> responseServer;
    public MutableLiveData<Integer> userId;
    public MutableLiveData<Integer> currentChat;
    private MutableLiveData<Integer> currentUser;
    private MutableLiveData<Boolean> needUpdateModel;


    public ListUsersViewModel(){

        responseServer = new MutableLiveData<>();
        userId = new MutableLiveData<>();
        currentChat = new MutableLiveData<>();
        currentUser = new MutableLiveData<>();
        needUpdateModel = new MutableLiveData<>(true);
    }

    public void setCurrentUser(Integer currentUserId){
        this.currentUser.setValue(currentUserId);
    }
    public Integer getCurrentUser(){
        return this.currentUser.getValue();
    }

    public void setNeedUpdate(boolean needUpdate){
        this.needUpdateModel.setValue(needUpdate);
    }
    public boolean needUpdate(){
        return needUpdateModel.getValue();
    }

    public void setResponseServer(ResponseServer responseServer){
        this.responseServer.setValue(responseServer);
    }
    public ResponseServer getResponseServer(){
        return responseServer.getValue();
    }

    public void setCurrentChat(int chatId){
        this.currentChat.setValue(chatId);
    }
    public void setUserId(int userId){
        this.userId.setValue(userId);
    }

    public Integer getCurrentChat(){
        return currentChat.getValue();
    }
}