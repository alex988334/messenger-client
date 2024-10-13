package com.orion.messenger.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentChatBinding;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.MessageStatus;
import com.orion.messenger.ui.message.MessageViewModel;
import com.orion.messenger.ui.user.AboutUserViewModel;
import com.orion.messenger.ui.user.ListUsersViewModel;

public class ChatFragment extends Fragment {

    public static final String CHAT_ID = "chat_id";

    public static final int ID = 1;

    private FragmentChatBinding binding;
    private ChatViewModel model;
    private LayoutInflater inflater;


    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        model = provider.get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (model.responseServer == null) {
            Log.e(ConstWSThread.LOG_TAG, "model.responseServer == null");
        }

        binding.createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChatDialog();
            }
        });
        binding.chatRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.GET_LIST_CHATS, new Bundle());
            }
        });

        binding.spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int chatId = (Integer) view.getTag();
                String chatAlias = ((TextView)view.findViewById(R.id.chat_name)).getText().toString();

                ViewModelProvider provider = new ViewModelProvider(requireActivity());
                ChatViewModel mChats = provider.get(ChatViewModel.class);
                MessageViewModel mMessages = provider.get(MessageViewModel.class);
                ListUsersViewModel mUsers = provider.get(ListUsersViewModel.class);

                Bundle bundle = new Bundle();

                if (mChats.getCurrentChat() == null || mChats.getCurrentChat() != chatId) {

                    mMessages.setResponseServer(new ResponseServer());
                    mMessages.setCurrentMessageInd(0);
                    mMessages.setCurrentMessage(0);

                    mChats.setCurrentChat(chatId);
                    mChats.setCurrentChatInd(i);

                    mChats.setCurrentChatName(chatAlias);
                    mUsers.setNeedUpdate(true);

                    bundle.putBoolean(ChatsContainerFragment.PARAM_NEED_UPDATE, true);
                } else {
                    mUsers.setNeedUpdate(false);
                    bundle.putBoolean(ChatsContainerFragment.PARAM_NEED_UPDATE, false);
                    mMessages.setCurrentMessageInd(mMessages.getResponseServer().Message.size()-1);
                }

                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_MESSAGES, bundle);
            }
        });

        model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {

            AboutUserViewModel user = provider.get(AboutUserViewModel.class);

            if (binding.chatRefresh.isRefreshing()) {
                binding.chatRefresh.setRefreshing(false);
            }

            AdapterListChats adapter = new AdapterListChats(inflater);
            adapter.setData(responseServer);
            binding.spisok.setAdapter(adapter);

            Bundle b = new Bundle();
            b.putString(ChatsContainerFragment.PARAM_MESSAGE_STATUS, MessageStatus.MESSAGE_DELIVERED);
            getParentFragmentManager().setFragmentResult(ChatsContainerFragment.CHANGE_MESSAGE_STATUS, b);
        });

        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.GET_LIST_CHATS, new Bundle());

        return root;
    }

    private void createNewChatDialog(){

        ChatFragment fr = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.create_chat_title);
        View v = inflater.inflate(R.layout.create_chat, null);
        EditText t = v.findViewById(R.id.create_chat_name);

        builder.setView(v);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String name = t.getText().toString();

                if (!name.isEmpty()) {
                    Bundle b = new Bundle();
                    b.putString(ChatsContainerFragment.PARAM_CHAT_NAME, name);
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.CREATE_NEW_CHAT, b);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.setIcon(R.drawable.ic_copyright);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* if (model == null) {
            model = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        }   */
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}