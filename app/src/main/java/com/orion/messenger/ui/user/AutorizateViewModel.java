package com.orion.messenger.ui.user;

import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AutorizateViewModel extends ViewModel {

    private MutableLiveData<String> login;
    private MutableLiveData<String> password;

    public AutorizateViewModel(){
        login = new MutableLiveData<>("");
        password = new MutableLiveData<>("");
    }

    public MutableLiveData<String> getLoginStorage() {
        return login;
    }
    public MutableLiveData<String> getPasswordStorage() {
        return password;
    }

    public String getLogin() {
        return login.getValue();
    }
    public void setLogin(String login) {
        this.login.setValue(login);
    }
    public String getPassword() {
        return password.getValue();
    }
    public void setPassword(String password) {
        this.password.setValue(password);
    }
}