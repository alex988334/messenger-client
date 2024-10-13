package com.orion.messenger.ui.user;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orion.messenger.R;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.ChatUser;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.data.UserPhone;

import java.util.List;

public class AdapterSearchUser extends BaseAdapter {


    private ResponseServer response;
    private boolean[] listUsersState;
    private LayoutInflater inflater;

    public AdapterSearchUser(LayoutInflater inflater){

        response = new ResponseServer();
        listUsersState = new boolean[response.User.size()];
        this.inflater = inflater;
    }

    public void setData(ResponseServer response){

        if (!response.User.isEmpty()) {
            listUsersState = new boolean[response.User.size()];
        } else if (!response.UserPhone.isEmpty()) {
            listUsersState = new boolean[response.UserPhone.size()];
        } else {
            //  TODO
        }

        this.response = response;
    }

    @Override
    public int getCount() {

        int count = response.User.size();

        if (count == 0) {
            count = response.UserPhone.size();
        }
        return count;
    }

    @Override
    public Object getItem(int i) {

        if (response.User.size() > i) {
            return response.User.get(i);
        }
        if (response.UserPhone.size() > i) {
            return response.UserPhone.get(i);
        }

        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LinearLayout viewIt =
                (LinearLayout) inflater.inflate(R.layout.search_user_item, viewGroup, false);

        if (response.User.size() > i) {
            User u = response.User.get(i);
            viewIt.setTag(u.Id);
            ((TextView) viewIt.findViewById(R.id.append_user_alias)).setText(u.Alias);
        } if (response.UserPhone.size() > i) {
            ((TextView) viewIt.findViewById(R.id.append_user_alias)).setVisibility(View.GONE);
            UserPhone phone = response.UserPhone.get(i);
            viewIt.setTag(phone.UserId);
            ((TextView) viewIt.findViewById(R.id.append_user_phone)).setText(phone.Phone);
        }

        if (listUsersState[i]) {
            viewIt.setBackgroundColor(Color.GRAY);
        } else {
            viewIt.setBackgroundColor(Color.WHITE);
        }

        return viewIt;
    }
    public void setSelectedUserPosition(int positionselected){

        for (int i=0; i < listUsersState.length; i++) {
            listUsersState[i] = false;
        }

        listUsersState[positionselected] = true;
    }
    public Integer getSelectedUserId(){

        for (int i=0; i < listUsersState.length; i++) {
            if (listUsersState[i]){
                if (response.User.size() > i) {
                    return response.User.get(i).Id;
                }
                if (response.UserPhone.size() > i) {
                    return response.UserPhone.get(i).UserId;
                }
            }
        }

        return null;
    }
}
