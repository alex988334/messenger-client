package com.orion.messenger.ui.user;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentAppendUserBinding;
import com.orion.messenger.databinding.FragmentMessageBinding;
import com.orion.messenger.ui.chat.ChatHandler;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.data.UserPhone;

public class AppendUserFragment extends Fragment {

    public static final int ID = 7;

    private AppendUserViewModel model;
    private FragmentAppendUserBinding binding;
    private LayoutInflater inflater;

    public static AppendUserFragment newInstance() {
        return new AppendUserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        model = provider.get(AppendUserViewModel.class);

        binding = FragmentAppendUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.searchListUsers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {

            AdapterSearchUser adapter = new AdapterSearchUser(inflater);

          /*  UserPhone u = new UserPhone();
            u.Phone = "53454323";
            u.UserId = 345;
            responseServer.UserPhone.add(u);
            u = new UserPhone();
            u.Phone = "98878734";
            u.UserId = 245;
            responseServer.UserPhone.add(u);*/

            adapter.setData(responseServer);
            binding.searchListUsers.setAdapter(adapter);
        });

        binding.searchListUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                model.setCurrentUser((int) view.getTag());
                AdapterSearchUser adapter = (AdapterSearchUser) binding.searchListUsers.getAdapter();

                adapter.setSelectedUserPosition(i);
                binding.searchListUsers.invalidateViews();

                return false;
            }
        });

        binding.searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                model.setCurrentUser(null);

                String alias = binding.searchAlias.getText().toString();
                String phone = binding.searchPhone.getText().toString();

                Bundle b = new Bundle();

                if (alias.isEmpty() && phone.isEmpty()) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT,
                            "To search, you need the user's alias or the user's phone number");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }

                model.setSearchAlias(alias);
                model.setSearchPhone(phone);

                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SEARCH_USER, new Bundle());
            }
        });

        binding.appendUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();

                Integer selectionUserId = model.getCurrentUser();

                if (selectionUserId == null) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "User not selected");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }

                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.APPEND_USER, b);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* if (mViewModel == null) {
            mViewModel = new ViewModelProvider(this).get(SearchUserViewModel.class);
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}