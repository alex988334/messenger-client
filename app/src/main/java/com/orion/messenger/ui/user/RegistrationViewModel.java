package com.orion.messenger.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {

    public MutableLiveData<String> login;
    public MutableLiveData<String> alias;
    public MutableLiveData<String> email;
    public MutableLiveData<String> password;
    public MutableLiveData<String> password1;

    public RegistrationViewModel(){}
}