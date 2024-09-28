package com.orion.messenger.ui.user;

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
import com.orion.messenger.databinding.ActivityMainBinding;
import com.orion.messenger.databinding.FragmentAutorizateBinding;
import com.orion.messenger.ui.chat.ChatHandlerProperty;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.data.User;

public class AutorizateFragment extends Fragment {

    private FragmentAutorizateBinding binding;

    public static AutorizateFragment newInstance() {
        return new AutorizateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AutorizateViewModel model = new ViewModelProvider(this).get(AutorizateViewModel.class);
        binding = FragmentAutorizateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ChatHandlerProperty settings = MainActivity.activvity.chat.getSettings();
        if (settings.login != null && !settings.login.isEmpty()) {
            binding.login.setText(settings.login);
        }
        if (settings.password != null && !settings.password.isEmpty()) {
            binding.password.setText(settings.password);
        }

        binding.sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.login.getText().toString().isEmpty()) {
                   // Log.d(ConstWSThread.LOG_TAG, "Login is empty!");
                    MainActivity.activvity.chat.showMessage("Login is empty!");
                    return;
                }
                if (binding.password.getText().toString().isEmpty()) {
                    MainActivity.activvity.chat.showMessage("Password is empty!");
                    return;
                }

                User u = new User();
                u.Login = binding.login.getText().toString();
                u.PassHash = binding.password.getText().toString();
                settings.login = u.Login;
                settings.password = u.PassHash;

                MainActivity.activvity.chat.autorizationUser(u);
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}