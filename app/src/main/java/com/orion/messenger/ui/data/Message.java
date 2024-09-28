package com.orion.messenger.ui.data;

public class Message extends Model {

    public long Id;
    public int ChatId;
    public int Author;
    public long ParrentMessage;
    public String Message;
    public String FileUrl;
    public String Date;
    public String Time;

    public Message() {
        super(MODEL_MESSAGE);
    }

    @Override
    public String toString() {
        return "{\"Id\":"+Id+",\"ChatId\":"+ChatId+",\"Author\":"+Author+",\"ParrentMessage\":"+ParrentMessage
                +",\"Message\":\""+Message+"\",\"FileUrl\":\""+FileUrl+"\",\"Date\":\""+Date
                +"\",\"Time\":\""+Time+"\"," + super.toString() + "}";
    }
}
