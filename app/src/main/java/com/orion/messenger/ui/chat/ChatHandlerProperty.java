package com.orion.messenger.ui.chat;

import androidx.lifecycle.ViewModel;

import com.orion.messenger.ui.user.AboutUserViewModel;
import com.orion.messenger.ui.user.AutorizateViewModel;
import com.orion.messenger.ui.user.RegistrationViewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatHandlerProperty {

    public Integer userId;
    public String login;
    public String alias;
    public String password;
    public String authKey;
    public String email;

    public ChatHandlerProperty() {
        userId = 0;
        login = "";
        alias = "";
        password = "";
        authKey = "";
        email = "";
    }

    public void loadFromViewModel(ViewModel model) {

        if (model instanceof AutorizateViewModel) {

            AutorizateViewModel m = (AutorizateViewModel) model;
            if (!m.getLogin().isEmpty()) {
                login = m.getLogin();
            }
            if (!m.getPassword().isEmpty()) {
                password = m.getPassword();
            }
        }

        if (model instanceof RegistrationViewModel) {

            RegistrationViewModel m = (RegistrationViewModel) model;
            if (!m.getLogin().isEmpty()) {
                login = m.getLogin();
            }
            if (!m.getPassword().isEmpty()) {
                password = m.getPassword();
            }
            if (!m.getAlias().isEmpty()) {
                alias = m.getAlias();
            }
            if (!m.getEmail().isEmpty()) {
                email = m.getEmail();
            }
        }

        if (model instanceof AboutUserViewModel) {

            AboutUserViewModel m = (AboutUserViewModel) model;
            if (m.getUserId() != 0) {
                userId = m.getUserId();
            }
            if (!m.getLogin().isEmpty()) {
                login = m.getLogin();
            }
            if (!m.getPassword().isEmpty()) {
                password = m.getPassword();
            }
            if (!m.getAlias().isEmpty()) {
                alias = m.getAlias();
            }
            if (!m.getEmail().isEmpty()) {
                email = m.getEmail();
            }
        }
    }

}
