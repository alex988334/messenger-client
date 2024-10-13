package com.orion.messenger.ui.user;

import com.orion.messenger.ui.data.User;

public class AdapterUserModel {

    public User loadUser(RegistrationViewModel model) {

        User u = new User();
        u.Login = (model.getLogin() != null) ? model.getLogin() : "";
        u.Alias = (model.getAlias() != null) ? model.getAlias() : "";
        u.Email = (model.getEmail() != null) ? model.getEmail() : "";
        u.PassHash = (model.getPassword() != null) ? model.getPassword() : "";

        return u;
    }
}
