package com.orion.messenger.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;

import com.orion.messenger.databinding.FragmentAboutUserBinding;

public class AboutUserFragment extends Fragment {

    private FragmentAboutUserBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        AboutUserViewModel model = new ViewModelProvider(this).get(AboutUserViewModel.class);

        binding = FragmentAboutUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.userCreated.setText(model.userCreatedAt.getValue());
        binding.userUpdated.setText(model.userUpdatedAt.getValue());
        binding.userLogin.setText(model.userLogin.getValue());
        binding.userAlias.setText(model.userAlias.getValue());
        binding.userEmail.setText(model.userEmail.getValue());
        binding.userPassword.setText(model.userPassword.getValue());
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
