package com.orion.messenger.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<String> login;
    private MutableLiveData<String> alias;
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;


    public RegistrationViewModel(){
        login = new MutableLiveData<>("");
        alias = new MutableLiveData<>("");
        email = new MutableLiveData<>("");
        password = new MutableLiveData<>("");
    }

    public String getLogin() {
        return login.getValue();
    }
    public void setLogin(String login) {
        this.login.setValue(login);
    }
    public String getAlias() {
        return alias.getValue();
    }
    public void setAlias(String alias) {
        this.alias.setValue(alias);
    }
    public String getEmail() {
        return email.getValue();
    }
    public void setEmail(String email) {
        this.email.setValue(email);
    }
    public String getPassword() {
        return password.getValue();
    }
    public void setPassword(String password) {
        this.password.setValue(password);
    }
}