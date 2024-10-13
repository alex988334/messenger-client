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

    public static final int ID = 4;

    private FragmentAboutUserBinding binding;
    private AboutUserViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // super.onCreateView(inflater, container, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(AboutUserViewModel.class);

        binding = FragmentAboutUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.created.setText(model.getCreatedAt());
        binding.updated.setText(model.getUpdatedAt());
        binding.login.setText(model.getLogin());
        binding.alias.setText(model.getAlias());
        binding.email.setText(model.getEmail());
        binding.password.setText(model.getPassword());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
