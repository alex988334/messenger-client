package com.orion.messenger.ui.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orion.messenger.R;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.Chat;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.data.Model;
import com.orion.messenger.ui.data.User;

import java.util.ArrayList;
import java.util.List;

public class AdapterListMessage extends BaseAdapter {

    private ResponseServer response;
    private LayoutInflater inflater;
    private int userId;

    public AdapterListMessage(LayoutInflater inflater, int userId){

        response = new ResponseServer();
        response.Message = new Message[]{};
        this.inflater = inflater;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return response.Message.length;
    }

    @Override
    public Object getItem(int i) {
        return response.Message[i];
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

        Message message = response.Message[i];
        int idLayout = R.layout.message_item_right;

        boolean isAuthor = false;
        String mAuthor = null;
        Integer mStatus = null;
        if (message.Author == userId) {
            idLayout = R.layout.message_item_left;
            isAuthor = true;
        } else {
            mAuthor = findUserAlias(message.Author);
        }

        if (isAuthor && response.MessageStatus.length > 0) {
            String status = null;
            for (MessageStatus ms: response.MessageStatus) {
                if (ms.MessageId == message.Id) {
                    if (status == null) {
                        status = ms.Status;
                        continue;
                    }
                    if (ms.Status.equals(MessageStatus.MESSAGE_SENDED)) {
                       status = ms.Status;
                       mStatus = R.drawable.ic_status_send;
                    }
                    if (ms.Status.equals(MessageStatus.MESSAGE_DELIVERED) &&
                            !status.equals(MessageStatus.MESSAGE_SENDED)) {
                        status = ms.Status;
                        mStatus = R.drawable.ic_status_delivered;
                    }
                    if (ms.Status.equals(MessageStatus.MESSAGE_READED) &&
                            !status.equals(MessageStatus.MESSAGE_SENDED) &&
                            !status.equals(MessageStatus.MESSAGE_DELIVERED)) {
                        status = ms.Status;
                        mStatus = R.drawable.ic_status_readed;
                    }
                }
            }
        }

        Message mParent = null;
        String mParentAuthor = null;

        if (message.ParrentMessage > 0) {
            for (Message m: response.Message) {
                if (m.Id == message.ParrentMessage) {
                    mParent = m;
                }
            }
            mParentAuthor = findUserAlias(mParent.Author);
        }

        LinearLayout viewIt =
                (LinearLayout) inflater.inflate(idLayout, viewGroup, false);
        viewIt.setTag(message.Id);

        if (!isAuthor) {
            ((TextView) viewIt.findViewById(R.id.message_author)).setText(mAuthor);
        }
        ((TextView) viewIt.findViewById(R.id.message_text)).setText(message.Message);
        View v = viewIt.findViewById(R.id.message_time);
        ((TextView) v).setText(message.Time);
        v.setTag(message.Date);

        if (isAuthor && mStatus != null) {
            ((ImageView) v.findViewById(R.id.message_status)).setImageResource(mStatus);
        }

        if (mParent != null) {
            if (mParentAuthor != null) {
                ((TextView) viewIt.findViewById(R.id.message_parent_user)).setText(mParentAuthor);
            }
            ((TextView) viewIt.findViewById(R.id.message_parent_text)).setText(mParent.Message);
        }

        return viewIt;
    }
}
