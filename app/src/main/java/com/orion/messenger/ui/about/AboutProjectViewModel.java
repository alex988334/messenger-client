package com.orion.messenger.ui.about;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutProjectViewModel extends ViewModel {

    public MutableLiveData<String> description;

    public AboutProjectViewModel(){
        description = new MutableLiveData<>("");
    }
}