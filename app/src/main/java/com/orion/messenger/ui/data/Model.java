package com.orion.messenger.ui.data;

public class Model implements IModel {

    public static final String MODEL_USER           = "User";
    public static final String MODEL_CHAT_USER      = "ChatUser";
    public static final String MODEL_CHAT           = "Chat";
    public static final String MODEL_MESSAGE        = "Message";
    public static final String MODEL_STATUS_MESSAGE = "MessageStatus";
    public static final String MODEL_BLACK_LIST     = "BlackList";
    public static final String MODEL_USER_PHONE     = "UserPhone";
    public static final String MODEL_STATUS         = "Status";
    public static final String MODEL_SYSTEM         = "System";

    private int operation;
    private String nameModel;

    @Override
    public int GetOperation() {
        return operation;
    }

    @Override
    public void SetOperation(int operation) {
        this.operation = operation;
    }

    @Override
    public String NameModel() {
        return nameModel;
    }

    @Override
    public void SetNameModel(String nameModel) {
        this.nameModel = nameModel;
    }

    public Model(String nameModel) {
        this.nameModel = nameModel;
    }

    @Override
    public String toString() {
        return "\"operation\":"+operation+",\"nameModel\":\""+nameModel+"\"";
    }
}
