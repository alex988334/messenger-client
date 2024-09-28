package com.orion.messenger.ui.user;

import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AutorizateViewModel extends ViewModel {

    public MutableLiveData<String> login;
    public MutableLiveData<String> password;
  //  public MutableLiveData<Button> send;

    public AutorizateViewModel(){}
}