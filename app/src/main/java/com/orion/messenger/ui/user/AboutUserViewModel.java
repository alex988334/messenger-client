package com.orion.messenger.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutUserViewModel extends ViewModel {

    private MutableLiveData<Integer> userId;
    private MutableLiveData<String> login;
    private MutableLiveData<String> alias;
    private MutableLiveData<String> email;
    private MutableLiveData<String> password;
    private MutableLiveData<String> createdAt;
    private MutableLiveData<String> updatedAt;

    public AboutUserViewModel(){
        userId = new MutableLiveData<>(new Integer(0));
        login = new MutableLiveData<>("");
        alias = new MutableLiveData<>("");
        email = new MutableLiveData<>("");
        password = new MutableLiveData<>("");
        createdAt = new MutableLiveData<>("");
        updatedAt = new MutableLiveData<>("");
    }
    public int getUserId() {
        return userId.getValue();
    }
    public void setUserId(int userId) {
        this.userId.setValue(userId);
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
    public String getCreatedAt() {
        return createdAt.getValue();
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt.setValue(createdAt);
    }
    public String getUpdatedAt() {
        return updatedAt.getValue();
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt.setValue(updatedAt);
    }
}
