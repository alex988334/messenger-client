package com.orion.messenger.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.orion.messenger.MainActivity;
import com.orion.messenger.databinding.FragmentRegistrationBinding;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.data.User;

public class RegistrationFragment extends Fragment {

    public static final int ID = 6;

    private FragmentRegistrationBinding binding;
    private RegistrationViewModel model;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        model = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });

        return root;
    }

    private void registerNewUser() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        model.setAlias(binding.alias.getText().toString());
        model.setLogin(binding.login.getText().toString());
        model.setEmail(binding.email.getText().toString());
        model.setPassword(binding.password.getText().toString());
        String passRepeat = binding.password1.getText().toString();

        Bundle b = new Bundle();

        if (model.getAlias().isEmpty() || model.getLogin().isEmpty()
                || model.getEmail().isEmpty() || model.getPassword().isEmpty()) {

            b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "One or more fields are empty!");
            getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_MESSAGES, b);
            return;
        }

        if (!model.getPassword().equals(passRepeat)) {
            b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "Password and repeat password is not equal!");
            getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_MESSAGES, b);
            return;
        }

        b.putString(ChatsContainerFragment.PARAM_MESSAGE_TEXT, "Please wait, processing in progress!");
        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.SHOW_NOTIFICATION, b);
        getParentFragmentManager().setFragmentResult(ChatsContainerFragment.REGISTRATION, b);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* if (model == null) {
            model = new ViewModelProvider(this).get(RegistrationViewModel.class);
        }*/
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}