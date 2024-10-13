package com.orion.messenger.ui.user;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orion.messenger.MainActivity;
import com.orion.messenger.databinding.FragmentAutorizateBinding;
import com.orion.messenger.ui.chat.ChatHandlerProperty;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.data.User;

public class AutorizateFragment extends Fragment {

    public static final int ID = 5;

    private FragmentAutorizateBinding binding;

    public static AutorizateFragment newInstance() {
        return new AutorizateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        AutorizateViewModel model = new ViewModelProvider(requireActivity()).get(AutorizateViewModel.class);
        binding = FragmentAutorizateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.login.setText(model.getLogin());
        binding.password.setText(model.getPassword());

        model.getPasswordStorage().observe(getViewLifecycleOwner(), value -> {
            binding.login.setText(model.getLogin());
            binding.password.setText(model.getPassword());
        });

        binding.sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();

                if (binding.login.getText().toString().isEmpty()) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "Login is empty!");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }
                if (binding.password.getText().toString().isEmpty()) {
                    b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "Password is empty!");
                    getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
                    return;
                }
                getParentFragmentManager().setFragmentResult(ChatsContainerFragment.AUTORIZATE, b);
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