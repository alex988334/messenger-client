package com.orion.messenger.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutUserViewModel extends ViewModel {

    public MutableLiveData<String> userLogin;
    public MutableLiveData<String> userAlias;
    public MutableLiveData<String> userEmail;
    public MutableLiveData<String> userPassword;
    public MutableLiveData<String> userCreatedAt;
    public MutableLiveData<String> userUpdatedAt;

    public AboutUserViewModel(){
        userLogin = new MutableLiveData<>("Alex");
        userAlias = new MutableLiveData<>("Mad Alex");
        userEmail = new MutableLiveData<>("madalex@gmail.com");
        userPassword = new MutableLiveData<>("WERWERWERR");
        userCreatedAt = new MutableLiveData<>("2016-05-28");
        userUpdatedAt = new MutableLiveData<>("2016-07-20");
    }
}
