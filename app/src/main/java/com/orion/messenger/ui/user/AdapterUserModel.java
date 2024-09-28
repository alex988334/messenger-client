package com.orion.messenger.ui.user;

import com.orion.messenger.ui.data.User;

public class AdapterUserModel {

    public User loadUser(RegistrationViewModel model) {

        User u = new User();
        u.Login = (model.login.getValue() != null) ? model.login.getValue() : "";
        u.Alias = (model.alias.getValue() != null) ? model.alias.getValue() : "";
        u.Email = (model.email.getValue() != null) ? model.email.getValue() : "";
        u.PassHash = (model.password.getValue() != null) ? model.password.getValue() : "";

        return u;
    }
}
