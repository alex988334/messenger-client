package com.orion.messenger.ui.chat;

public interface IGenerateUI extends IAutorizate, IRegister {

    void runOnUiThread(Runnable runnable);
    void showMessage(String msg);
}
