package com.orion.messenger.ui.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orion.messenger.R;
import com.orion.messenger.ui.message.MessageFragment;
import com.orion.messenger.ui.message.MessageViewModel;
import com.orion.messenger.ui.user.AppendUserFragment;
import com.orion.messenger.ui.user.AppendUserViewModel;
import com.orion.messenger.ui.user.ListUsersFragment;
import com.orion.messenger.ui.user.ListUsersViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsContainerFragment extends Fragment {

    public static final String SHOW_NOTIFICATION = "show_notify";

    public static final String AUTORIZATE = "autorizate";
    public static final String REGISTRATION= "registrate";
    public static final String ABOUT_USER= "about_user";

    public static final String SHOW_CHATS = "show_chats";
    public static final String SHOW_MESSAGES = "show_messages";
    public static final String SHOW_CHAT_USERS = "show_users";
    public static final String SHOW_APPEND_USERS = "show_append_users";

    public static final String CLOSE_CHAT_USERS = "close_chat_users";

    public static final String GET_LIST_CHATS = "get_list_chats";
    public static final String GET_LIST_MESSAGES = "get_list_messages";
    public static final String GET_LIST_USERS = "get_list_users";

    public static final String CHANGE_MESSAGE_STATUS = "change_message_status";

    public static final String CREATE_NEW_CHAT = "new_chat";
    public static final String CREATE_NEW_MESSAGE = "new_message";

    public static final String APPEND_USER = "append_user";
    public static final String REMOVE_USER = "remove_user";
    public static final String SEARCH_USER = "search_user";
    public static final String EXIT_CHAT = "exit_chat";

    public static final String PARAM_NEED_UPDATE = "need_update";
    public static final String PARAM_CHAT_NAME = "chat_name";
    public static final String PARAM_MESSAGE_TEXT = "message_text";
    public static final String PARAM_PARENT_MESSAGE = "parent_message";
    public static final String PARAM_MESSAGE_STATUS = "message_status";

    public String CHAT_FRAGMENT = "chat_fragment";
    public static final String MESSAGE_FRAGMENT = "message_fragment";
    public static final String USER_FRAGMENT = "user_fragment";
    public static final String APPEND_USER_FRAGMENT = "append_user_fragment";

  /*  // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    public ChatsContainerFragment() {
        super();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager manager = getChildFragmentManager();

        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment current = manager.findFragmentById(R.id.chat_container_fragment);
                if (current !=  null && (current instanceof ChatFragment)) {
                    getParentFragmentManager().setFragmentResult(SHOW_CHATS, new Bundle());
                    return;
                }
                if (current !=  null && (current instanceof MessageFragment)) {
                    (new ViewModelProvider(requireActivity()).get(ListUsersViewModel.class)).setCurrentUser(null);
                    getParentFragmentManager().setFragmentResult(SHOW_MESSAGES, new Bundle());
                    return;
                }
                if (current !=  null && (current instanceof ListUsersFragment)) {
                    (new ViewModelProvider(requireActivity()).get(AppendUserViewModel.class)).setCurrentUser(null);
                    (new ViewModelProvider(requireActivity()).get(ListUsersViewModel.class)).setCurrentUser(null);
                    getParentFragmentManager().setFragmentResult(SHOW_CHAT_USERS, new Bundle());
                    getParentFragmentManager().setFragmentResult(GET_LIST_USERS, new Bundle());
                    return;
                }
            }
        });

        manager.setFragmentResultListener(CLOSE_CHAT_USERS,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        exitUserUI();
                    }
                });

        manager.setFragmentResultListener(CHANGE_MESSAGE_STATUS,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(CHANGE_MESSAGE_STATUS, result);
                    }
                });
        manager.setFragmentResultListener(EXIT_CHAT,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(EXIT_CHAT, result);
                    }
                });
        manager.setFragmentResultListener(SEARCH_USER,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(SEARCH_USER, result);
                    }
                });
        manager.setFragmentResultListener(APPEND_USER,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(APPEND_USER, result);
                    }
                });
        manager.setFragmentResultListener(REMOVE_USER,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(REMOVE_USER, result);
                    }
                });

        manager.setFragmentResultListener(GET_LIST_CHATS,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(GET_LIST_CHATS, new Bundle());
                    }
                });
        manager.setFragmentResultListener(GET_LIST_MESSAGES,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getParentFragmentManager().setFragmentResult(GET_LIST_MESSAGES, new Bundle());
                    }
                });

        manager.setFragmentResultListener(SHOW_MESSAGES,
                requireActivity(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                if (result.getBoolean(PARAM_NEED_UPDATE)) {
                    getParentFragmentManager().setFragmentResult(GET_LIST_MESSAGES, new Bundle());
                }
                createMessageUI();
            }
        });

        manager.setFragmentResultListener(SHOW_CHAT_USERS,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        if (result.getBoolean(PARAM_NEED_UPDATE)) {
                            getParentFragmentManager().setFragmentResult(GET_LIST_MESSAGES, new Bundle());
                        }
                        createMessageUI();
                    }
                });
        manager.setFragmentResultListener(SHOW_APPEND_USERS,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        createAppendUsersUI();
                    }
                });

        manager.setFragmentResultListener(CREATE_NEW_CHAT,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        getParentFragmentManager().setFragmentResult(CREATE_NEW_CHAT, result);
                    }
                });

        manager.setFragmentResultListener(CREATE_NEW_MESSAGE,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        getParentFragmentManager().setFragmentResult(CREATE_NEW_MESSAGE, result);
                    }
                });
        manager.setFragmentResultListener(SHOW_NOTIFICATION,
                requireActivity(), new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        getParentFragmentManager().setFragmentResult(SHOW_NOTIFICATION, result);
                    }
                });
    }

    /**
     * @return A new instance of fragment ChatsContainerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsContainerFragment newInstance(String param1, String param2) {
        ChatsContainerFragment fragment = new ChatsContainerFragment();
      /*  Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chats_container, container, false);

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_CHATS, new Bundle());

        createChatUI();
    }

    private void exitUserUI() {

        FragmentManager manager = getChildFragmentManager();

        manager.beginTransaction()
                .remove(manager.findFragmentByTag(USER_FRAGMENT))
                .commit();
        createMessageUI();
    }

    public void createChatUI() {

        FragmentManager manager = getChildFragmentManager();
        Fragment fr = manager.findFragmentById(R.id.chat_fragment);
        if (fr == null) {
            fr = new ChatFragment();
        }

        manager.beginTransaction()
                .add(R.id.chat_container_fragment, fr, CHAT_FRAGMENT)
                .commit();
    }

    public void createMessageUI(){

        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_MESSAGES, new Bundle());

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.chat_container_fragment, new MessageFragment(), MESSAGE_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void createAppendUsersUI(){

        (new ViewModelProvider(requireActivity()))
                .get(AppendUserViewModel.class).setResponseServer(new ResponseServer());

        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_APPEND_USERS, new Bundle());

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.chat_container_fragment, new AppendUserFragment(), APPEND_USER_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void createChatUsersUI(){

        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_CHAT_USERS, new Bundle());

        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.chat_container_fragment, new ListUsersFragment(), USER_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }
}