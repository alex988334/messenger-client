package com.orion.messenger.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orion.messenger.R;
import com.orion.messenger.ui.data.Chat;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.data.User;

public class AdapterListChats extends BaseAdapter {

    private ResponseServer response;
    private LayoutInflater inflater;

    public AdapterListChats(LayoutInflater inflater){

        response = new ResponseServer();
        response.Chat = new Chat[]{};
        this.inflater = inflater;
    }

    public ResponseServer getData(){
        return response;
    }

    public void setData(ResponseServer response) {
        this.response = response;
    }

    @Override
    public int getCount() {
        return response.Chat.length;
    }

    @Override
    public Object getItem(int i) {
        return response.Chat[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LinearLayout viewIt = (LinearLayout)
                inflater.inflate(R.layout.chat_item, viewGroup, false);

        Chat chat = response.Chat[i];
        viewIt.setTag(chat.Id);

        ((TextView) viewIt.findViewById(R.id.chat_name)).setText(chat.Name);

        Long lastMessageId = null;
        Integer authorMessage = null;

        if (response.Message != null) {
            for (Message m: response.Message) {

                if (chat.Id == m.ChatId) {
                    lastMessageId = m.Id;
                    authorMessage = m.Author;
                    TextView mess = (TextView) viewIt.findViewById(R.id.chat_message_message);
                    mess.setTag(m.Id);
                    mess.setText(m.Message);
                    ((TextView) viewIt.findViewById(R.id.chat_message_date)).setText(m.Date);
                    break;
                }
            }
        }

        if (lastMessageId != null && response.MessageStatus != null) {

            String status = "";
            for (MessageStatus ms: response.MessageStatus) {
                if (lastMessageId == ms.MessageId) {
                    if (!ms.Status.equals(MessageStatus.MESSAGE_READED)) {
                        ((RelativeLayout) viewIt.findViewById(R.id.chat_alarm_container)).setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        if (authorMessage != null && response.User != null) {

            for (User u: response.User) {
                if (authorMessage == u.Id) {
                    ((TextView) viewIt.findViewById(R.id.chat_message_author)).setText(u.Alias);
                }
            }
        }

        return viewIt;
    }
}