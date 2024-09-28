package com.orion.messenger.ui.data;

public class ChatUser extends Model {

    public int Chat;
    public int User;
    public String SessionHash;

    public ChatUser() {
        super(MODEL_CHAT_USER);
    }

    @Override
    public String toString() {
        return "{\"Chat\":"+Chat+",\"User\":"+User+",\"SessionHash\":\""+SessionHash+"\","+
                super.toString() + "}";
    }
}
