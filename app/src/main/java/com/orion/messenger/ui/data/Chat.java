package com.orion.messenger.ui.data;

import java.util.Objects;

public class Chat extends Model {

    public int Id;
    public int Author;
    public String Name;
    public String CreateAt;
    public String Status;

    public Chat() {
        super(MODEL_CHAT);
    }

    @Override
    public String toString() {
        return "{\"Id\":"+Id+",\"Author\":"+Author+",\"Name\":\""+Name+"\",\"CreateAt\":\""
                +CreateAt+"\",\"Status\":\""+Status+"\"," + super.toString() + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Chat)) {
            return false;
        }

        if (Id != 0 && ((Chat) object).Id == Id) {
            return true;
        }

        return false;
    }
}
