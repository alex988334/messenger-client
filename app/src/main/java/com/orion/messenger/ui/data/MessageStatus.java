package com.orion.messenger.ui.data;

public class MessageStatus extends Model {

    public static final String MESSAGE_CREATED = "created";
    public static final String MESSAGE_SENDED = "sended";
    public static final String MESSAGE_DELIVERED = "delivered";
    public static final String  MESSAGE_READED = "readed";
    public static final String MESSAGE_BLACK_LIST = "black_list";

    public long MessageId;
    public int UserId;
    public String Status;
    public String Date;
    public String Time;

    public MessageStatus() {
        super(MODEL_STATUS_MESSAGE);
    }

    @Override
    public String toString() {
        return "{\"MessageId\":"+MessageId+",\"UserId\":"+UserId+",\"Status\":\""+Status
                +"\",\"Date\":\""+Date+"\",\"Time\":\""+Time+"\"," + super.toString() + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MessageStatus)) {
            return false;
        }

        MessageStatus m = (MessageStatus) object;
        if (MessageId != 0 && UserId != 0 && m.MessageId == MessageId && m.UserId == UserId) {
            return true;
        }

        return false;
    }
}
