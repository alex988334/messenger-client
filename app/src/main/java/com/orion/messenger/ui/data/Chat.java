package com.orion.messenger.ui.data;

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
}
