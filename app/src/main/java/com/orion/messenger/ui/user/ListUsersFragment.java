package com.orion.messenger.ui.user;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.orion.messenger.MainActivity;
import com.orion.messenger.databinding.FragmentListUsersBinding;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.message.AdapterListMessage;

public class ListUsersFragment extends Fragment {

    public static final int ID = 3;

    private ListUsersViewModel model;
    private FragmentListUsersBinding binding;
    private LayoutInflater inflater;

    public static ListUsersFragment newInstance() {
        return new ListUsersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        model = provider.get(ListUsersViewModel.class);

        binding = FragmentListUsersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.usersList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                model.setCurrentUser((int) view.getTag());
                AdapterListUsers adapter = (AdapterListUsers) binding.usersList.getAdapter();

                adapter.setSelectedUserPosition(i);
                binding.usersList.invalidateViews();

                return false;
            }
        });

        binding.userListLeaveChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Сonfirmation of operation");
                builder.setMessage("Are you sure you want to leave the chat?");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.EXIT_CHAT, new Bundle());
                    }
                }).show();
            }
        });

        binding.userListRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();
                Integer selectionUserId = model.getCurrentUser();

                AboutUserViewModel mUser =
                        new ViewModelProvider(requireActivity()).get(AboutUserViewModel.class);
                ChatViewModel mChat = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

                int authorChat = mChat.getResponseServer().Chat.get(mChat.getCurrentChatInd()).Author;
                if (authorChat != mUser.getUserId()) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "You are not a chat moderator");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }

                if (selectionUserId == null) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "User not selected");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }

                String userAlias = "";
                for (User u: model.getResponseServer().User) {
                    if (u.Id == selectionUserId) {
                        userAlias = u.Alias;
                        break;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Remove user from chat");
                builder.setMessage("Are you sure you want to remove user \""+ userAlias +"\" from the chat?");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.REMOVE_USER, b);
                    }
                }).show();
            }
        });

        binding.listUsersBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   getParentFragmentManager().setFragmentResult(ChatsContainerFragment.CLOSE_CHAT_USERS, new Bundle());
            }
        });

        binding.userListAppend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_APPEND_USERS, new Bundle());
            }
        });

        model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {

            AboutUserViewModel mUser = provider.get(AboutUserViewModel.class);
            ChatViewModel mChat = provider.get(ChatViewModel.class);

            int authorChat = mChat.getResponseServer().Chat.get(mChat.getCurrentChatInd()).Author;

            AdapterListUsers adapter = new AdapterListUsers(getResources(), inflater, mUser.getUserId(), authorChat);
            adapter.setData(responseServer);
            binding.usersList.setAdapter(adapter);
        });

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /*
        if (mViewModel == null) {
            mViewModel = new ViewModelProvider(this).get(ListUsersViewModel.class);
        }*/
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

      //  MainActivity.activvity.getSupportActionBar().setTitle("List users");
   //     Log.v(ConstWSThread.LOG_TAG, "onAttach LIST USERS");
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  MainActivity.activvity.getSupportActionBar().setTitle("Messages");
    //    Log.v(ConstWSThread.LOG_TAG, "onDetach LIST USERS");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(ConstWSThread.LOG_TAG, "onPause LIST USERS");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(ConstWSThread.LOG_TAG, "onResume LIST USERS");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(ConstWSThread.LOG_TAG, "onStop LIST USERS");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(ConstWSThread.LOG_TAG, "onStart LIST USERS");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.v(ConstWSThread.LOG_TAG, "onHiddenChanged LIST USERS");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(ConstWSThread.LOG_TAG, "onConfigurationChanged LIST USERS");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v(ConstWSThread.LOG_TAG, "onViewStateRestored LIST USERS");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(ConstWSThread.LOG_TAG, "onSaveInstanceState LIST USERS");
    }
*/
}