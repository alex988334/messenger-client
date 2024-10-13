package com.orion.messenger.ui.chat;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IBufferWS {

    boolean haveSendMessage();
    boolean haveResponseData();

    void addSendMessage(Map<String, byte[]> data);
    void addResponseData(ResponseServer responseServer);

    ResponseServer getResponseData();
    Map<String, byte[]> getSendMessage();

    File getSendFile();
}
