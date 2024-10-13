package com.orion.messenger.ui.chat;

public interface ISupportOperation extends IAutorizate, IRegister, IChatUI, IGetterViewModels {

    void runOnUiThread(Runnable runnable);
    void showMessage(String msg);
    void saveSettings();
    void runProgress();
    void stopProgress();
}
