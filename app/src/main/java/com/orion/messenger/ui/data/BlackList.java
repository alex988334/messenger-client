package com.orion.messenger.ui.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlackList extends Model {

    public int User;
    public int BlockedUser;
    public String Date;
    public String Time;

    public BlackList() {
        super(MODEL_BLACK_LIST);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BlackList)) {
            return false;
        }

        BlackList b = (BlackList) obj;
        if (User == b.User && BlockedUser == b.BlockedUser && User != 0 && BlockedUser != 0) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "{\"User\":"+User+",\"BlockedUser\":"+BlockedUser+",\"Date\":\""
                +Date+"\",\"Time\":\""+Time+"\"," + super.toString() + "}";
    }
}
