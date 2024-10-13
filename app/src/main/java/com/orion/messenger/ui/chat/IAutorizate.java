package com.orion.messenger.ui.chat;

public interface IAutorizate {

    void createAutorizateUI();
    void loadModelsToSettings(ChatHandlerProperty settings);
    void onAutorization();
}
