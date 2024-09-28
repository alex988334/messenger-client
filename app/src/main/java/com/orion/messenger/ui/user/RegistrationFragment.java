package com.orion.messenger.ui.user;

import androidx.lifecycle.MutableLiveData;
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
import com.orion.messenger.databinding.FragmentRegistrationBinding;
import com.orion.messenger.ui.chat.ChatFragment;
import com.orion.messenger.ui.chat.ChatHandler;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.data.User;

public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private RegistrationViewModel model;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        model = new ViewModelProvider(this).get(RegistrationViewModel.class);
        binding = FragmentRegistrationBinding.inflate(inflater,container, false);
        View root = binding.getRoot();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(ConstWSThread.LOG_TAG, "=>>>>setChatHandlerpublic void onClick");
                registerNewUser();
            }
        });

        binding.email.setText("madmax@gmail.com");
        binding.alias.setText("Mad Max2343432432");
        binding.login.setText("Max124325743432432");
        binding.password.setText("123456789012345");
        binding.password1.setText("123456789012345");

        return root;
    }

    private void registerNewUser() {

        model.alias = new MutableLiveData<>(binding.alias.getText().toString());
        model.login = new MutableLiveData<>(binding.login.getText().toString());
        model.email = new MutableLiveData<>(binding.email.getText().toString());
        model.password = new MutableLiveData<>(binding.password.getText().toString());
        model.password1 = new MutableLiveData<>(binding.password1.getText().toString());

        if (model.alias.getValue().isEmpty() || model.login.getValue().isEmpty()
                || model.email.getValue().isEmpty() || model.password.getValue().isEmpty()) {
            MainActivity.activvity.showMessage("One or more fields are empty!");
            return;
        }

        if (!model.password.getValue().equals(model.password1.getValue())) {
            MainActivity.activvity.showMessage("Password and repeat password is not equal!");
            return;
        }

        User u = new AdapterUserModel().loadUser(model);

        MainActivity.activvity.chat.registrationUser(u);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(RegistrationViewModel.class);
        // TODO: Use the ViewModel
    }
}