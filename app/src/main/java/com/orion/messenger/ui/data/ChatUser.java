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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ChatUser)) {
            return false;
        }

        ChatUser u = (ChatUser) object;
        if (Chat != 0 && User != 0 && u.Chat == Chat && u.User == User) {
            return true;
        }

        return false;
    }
}
