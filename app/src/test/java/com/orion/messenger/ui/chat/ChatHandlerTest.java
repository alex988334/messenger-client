package com.orion.messenger.ui.chat;
/*
import com.google.gson.Gson;
import com.orion.messenger.ui.data.Model;
import com.orion.messenger.ui.data.User;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ChatHandlerTest extends TestCase {

    @Test
    public void newData() {

        Gson g = new Gson();

        StatusRequest st = new StatusRequest();
        st.Operation = ResponseServer.OP_REGISTRATION;

        User u = new User();
        u.Alias = "Mad Max2312424";
        u.Login = "Max4324356";
        u.Email = "max9292@gmail.com";
        u.PassHash = "uahmmsdkfyhqwp";

        Map<String, byte[]> data = new HashMap<>();
        data.put(Model.MODEL_STATUS, g.toJson(st).getBytes());
        data.put(Model.MODEL_USER, g.toJson(u).getBytes());

        ChatHandler h = new ChatHandler(new GenerateUI());
        h.appendResponseData(data);
        h.newData();
    }

    private class GenerateUI implements IGenerateUI {

        @Override
        public void runOnUiThread(Runnable runnable) {}

        @Override
        public void showMessage(String msg) {}

        @Override
        public void createAutorizateUI() {}

        @Override
        public void createRegisterUI() {}
    }
}*/