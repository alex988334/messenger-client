package com.orion.messenger.ui.settings;

import android.content.Context;

import com.orion.messenger.ui.chat.ChatHandlerProperty;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class SettingsHandlerTest extends TestCase {

    public void testWriteSettingsToFile() {

        ChatHandlerProperty settings = new ChatHandlerProperty();
        settings.alias = "Mad Max";
        settings.login = "Max";
        settings.currentChat = 2;
        settings.currentMessage = 20L;
        settings.userId = 1;
        settings.password = "albatros";
        settings.authKey = "qWretyuiioppppwew";

      //  SettingsHandler handler = new SettingsHandler(Context.);


    }

    public void testReadSettingsFile() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}