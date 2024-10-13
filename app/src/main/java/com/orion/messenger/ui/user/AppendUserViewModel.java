package com.orion.messenger.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orion.messenger.ui.chat.ResponseServer;

public class AppendUserViewModel extends ViewModel {

    private MutableLiveData<String> searchAlias;
    private MutableLiveData<String> searchPhone;
    private MutableLiveData<Integer> currentUser;

    public MutableLiveData<ResponseServer> responseServer;

    public AppendUserViewModel(){

        searchAlias = new MutableLiveData<>("");
        searchPhone = new MutableLiveData<>("");
        currentUser = new MutableLiveData<>();

        responseServer = new MutableLiveData<>(new ResponseServer());
    }

    public void setCurrentUser(Integer currentUserId){
        this.currentUser.setValue(currentUserId);
    }
    public Integer getCurrentUser(){
        return this.currentUser.getValue();
    }
    public void setSearchAlias(String searchAlias){
        this.searchAlias.setValue(searchAlias);
    }
    public String getSearchAlias(){
        return this.searchAlias.getValue();
    }
    public void setSearchPhone(String searchPhone){
        this.searchPhone.setValue(searchPhone);
    }
    public String getSearchPhone(){
        return this.searchPhone.getValue();
    }

    public void setResponseServer(ResponseServer response){
        this.responseServer.setValue(response);
    }
    public ResponseServer getResponseServer(){
        return this.responseServer.getValue();
    }
}