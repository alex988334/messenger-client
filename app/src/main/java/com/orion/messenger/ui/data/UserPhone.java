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
}
