package com.orion.messenger.ui.data;

public class UserPhone extends Model{

    public int UserId;
    public String Phone;

    public UserPhone() {
        super(MODEL_USER_PHONE);
    }

    @Override
    public String toString() {
        return "{\"UserId\":"+UserId+",\"Phone\":\""+Phone+"\"," + super.toString() + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserPhone)) {
            return false;
        }

        UserPhone m = (UserPhone) object;
        if (UserId != 0 && Phone != null && m.Phone != null
                && !Phone.equals("") && m.UserId == UserId && m.Phone.equals(Phone)) {
            return true;
        }

        return false;
    }
}
