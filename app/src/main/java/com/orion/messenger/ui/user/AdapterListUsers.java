package com.orion.messenger.ui.user;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.orion.messenger.R;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.ChatUser;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.User;

public class AdapterListUsers extends BaseAdapter {

    private ResponseServer response;
    private LayoutInflater inflater;
    private int userId;
    private int authorId;
    private boolean[] listUsersState;
    private Resources resources;

    public AdapterListUsers(Resources resources,LayoutInflater inflater,
                            int userId, int authorId){

        response = new ResponseServer();
        this.inflater = inflater;
        this.userId = userId;
        this.authorId = authorId;
        listUsersState = new boolean[response.User.size()];
        this.resources = resources;
    }

    public void setData(ResponseServer response){
        listUsersState = new boolean[response.User.size()];
        this.response = response;
    }

    @Override
    public int getCount() {
        return response.ChatUser.size();
    }

    @Override
    public Object getItem(int i) {
        return response.ChatUser.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private String findUserAlias(int userId) {

        String alias = null;
        if (response.User != null) {
            for (User u: response.User) {
                if (u.Id == userId) {
                    alias = u.Alias;
                    break;
                }
            }
        }
        return alias;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ChatUser cu = response.ChatUser.get(i);

        RelativeLayout viewIt =
                (RelativeLayout) inflater.inflate(R.layout.chat_user_item, viewGroup, false);

        viewIt.setTag(cu.User);

        String alias = findUserAlias(cu.User);
        if (alias == null) {
            alias = "user id: " + String.valueOf(cu.User);
        }
        ((TextView) viewIt.findViewById(R.id.chat_user_alias)).setText(alias);

        if (cu.User == authorId) {
            ((ImageView) viewIt.findViewById(R.id.chat_user_author)).setVisibility(View.VISIBLE);
        }

        if (listUsersState[i]) {
            viewIt.setBackgroundColor(resources.getColor(R.color.grey2, null));
        } else {
            viewIt.setBackgroundColor(resources.getColor(R.color.grey4, null));
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
