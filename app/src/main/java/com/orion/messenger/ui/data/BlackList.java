package com.orion.messenger.ui.data;

import androidx.annotation.NonNull;

public class BlackList extends Model {

    public int User;
    public int BlockedUser;
    public String Date;
    public String Time;

    public BlackList() {
        super(MODEL_BLACK_LIST);
    }

    @Override
    public String toString() {
        return "{\"User\":"+User+",\"BlockedUser\":"+BlockedUser+",\"Date\":\""
                +Date+"\",\"Time\":\""+Time+"\"," + super.toString() + "}";
    }
}
