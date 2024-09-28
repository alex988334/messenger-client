package com.orion.messenger.ui.message;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orion.messenger.MainActivity;
import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentChatBinding;
import com.orion.messenger.databinding.FragmentMessageBinding;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.data.Message;

public class MessageFragment extends Fragment {

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
        model = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (model.responseServer == null) {
            Log.e(ConstWSThread.LOG_TAG, "model.responseServer == null");
        }

       /* model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {


        }*/


        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = binding.editMessage.getText().toString();
                if (msg.length() == 0) {
                    MainActivity.activvity.showMessage("Sended message is empty!");
                    return;
                }

                Message m = new Message();
                m.Message = msg;
                MainActivity.activvity.chat.newMessage(m);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(MessageViewModel.class);
        // TODO: Use the ViewModel
    }

}