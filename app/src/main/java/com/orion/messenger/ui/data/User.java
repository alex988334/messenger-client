package com.orion.messenger.ui.data;

public class User extends Model{

    public int Id;
    public String Login;
    public String Alias;
    public String AuthKey;
    public String PassHash;
    public String PassResetToken;
    public String Email;
    public int Status;
    public long CreateAt;
    public long UpdateAt;

    public User() {
        super(MODEL_USER);
    }

    @Override
    public String toString() {
        return "{\"Id\":"+Id+",\"Login\":\""+Login+"\",\"Alias\":\""+Alias+"\",\"AuthKey\":\""+AuthKey
                +"\",\"PassHash\":\""+PassHash+"\",\"PassResetToken\":\""+PassResetToken
                +"\",\"Email\":\""+Email+"\",\"Status\":"+Status+",\"CreateAt\":"+CreateAt
                +",\"UpdateAt\":"+UpdateAt +"," + super.toString() + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }

        User m = (User) object;
        if (Id != 0 && m.Id == Id) {
            return true;
        }

        return false;
    }
}
