package com.orion.messenger.ui.message;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orion.messenger.MainActivity;
import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentMessageBinding;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.user.AboutUserFragment;
import com.orion.messenger.ui.user.AboutUserViewModel;
import com.orion.messenger.ui.user.AdapterSearchUser;

import java.util.Timer;
import java.util.TimerTask;

public class MessageFragment extends Fragment {

    public static final int ID = 2;

    private MessageViewModel model;
    private FragmentMessageBinding binding;
    private LayoutInflater inflater;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        ViewModelProvider provider = new ViewModelProvider(requireActivity());

        model = provider.get(MessageViewModel.class);
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ChatViewModel mChat = provider.get(ChatViewModel.class);
        binding.messengerChatName.setText(mChat.getCurrentChatName());

        binding.messageRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                long currentMessage = 0;
                ResponseServer r = model.responseServer.getValue();

                if (r != null && r.Message.size() > 0) {
                    model.setCurrentMessageInd(0);
                    model.setCurrentMessage(r.Message.get(0).Id);
                }

                Bundle b = new Bundle();
                b.putBoolean(ChatsContainerFragment.PARAM_NEED_UPDATE, true);

                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.GET_LIST_MESSAGES, b);

                (new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (binding != null && binding.messageRefresh != null) {
                            binding.messageRefresh.setRefreshing(false);
                        }
                    }
                }, 5000);
            }
        });

      /*  binding.spisok.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int currentIndex = -1;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (currentIndex == -1) {
                    currentIndex = firstVisibleItem;
                    return;
                }
                if (currentIndex != firstVisibleItem) {
                    currentIndex = firstVisibleItem;
                } else {
                    return;
                }

                if (currentIndex == 0) {
                    Log.v("", "GET PREVIOUS");
                    model.currentMessage.setValue(firstVisibleItem);
                    handler.getListMessages();
                }
                if ((currentIndex + visibleItemCount) == totalItemCount) {
                    model.currentMessage.setValue(currentIndex + visibleItemCount - 1);
                    Log.v("", "GET NEXT");
                    handler.getListMessages();
                }
            }
        });*/

        model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {

            AboutUserViewModel mUser = provider.get(AboutUserViewModel.class);

            AdapterListMessage adapter = new AdapterListMessage(inflater, mUser.getUserId());
            adapter.setData(responseServer);
            binding.spisok.setAdapter(adapter);

            Integer cv = model.getCurrentMessageInd();
            if (cv != null) {
                binding.spisok.setSelection(cv);
            }

            binding.messageRefresh.setRefreshing(false);

           /* (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.acceptNewRequest();
                }
            }, 500);*/
        });

        binding.spisok.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                LinearLayout v = (LinearLayout) inflater
                        .inflate(R.layout.message_parent_item, binding.messengerFooterContainer, false);

                String parentAuthorNickName = ((TextView) view.findViewById(R.id.message_author)).getText().toString();
                if (!parentAuthorNickName.isEmpty()) {
                    ((TextView) v.findViewById(R.id.message_parent_author)).setText(parentAuthorNickName + ":");
                }
                ((TextView) v.findViewById(R.id.message_parent_text))
                        .setText(((TextView) view.findViewById(R.id.message_text)).getText());

                long parentId = (long) view.getTag();
                v.setTag(parentId);
                binding.messengerFooterContainer.addView(v);

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        binding.messengerFooterContainer.removeView(view);
                        return true;
                    }
                });

                return true;
            }
        });

        binding.spisok.setOnScrollListener(new AbsListView.OnScrollListener() {

            private String currentDate = "";
            private int currentItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                ListAdapter adapter = binding.spisok.getAdapter();

                if (i2 > 0 && i != currentItem && adapter != null) {
                    currentItem = i;
                    Message m = (Message) adapter.getItem(i + i1 - 1);

                    if (!currentDate.equals(m.Date)) {
                        currentDate = m.Date;
                        binding.messengerDate.setText(currentDate);
                    }
                }
            }
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = binding.editMessage.getText().toString();
                Bundle b = new Bundle();

                if (msg.length() == 0) {

                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "Sended message is empty!");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }

                b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, msg);

                View parentView = binding.messengerFooterContainer.findViewById(R.id.message_parent_root);
                if (parentView != null) {
                    b.putLong(ChatsContainerFragment.PARAM_PARENT_MESSAGE, (long) parentView.getTag());
                    binding.messengerFooterContainer.removeView(parentView);

                }

                binding.editMessage.setText("");
                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.CREATE_NEW_MESSAGE, b);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     /*   if (model == null) {
            model = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        }     */
        // TODO: Use the ViewModel
    }


}