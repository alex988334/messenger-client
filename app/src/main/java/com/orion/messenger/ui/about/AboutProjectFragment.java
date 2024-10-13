package com.orion.messenger.ui.about;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentAboutProjectBinding;
import com.orion.messenger.databinding.FragmentAboutUserBinding;
import com.orion.messenger.databinding.FragmentMessageBinding;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.message.MessageViewModel;

public class AboutProjectFragment extends Fragment {

    public static final int ID = 23;

    private FragmentAboutProjectBinding binding;
    private AboutProjectViewModel model;

    public static AboutProjectFragment newInstance() {
        return new AboutProjectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());

        model = provider.get(AboutProjectViewModel.class);
        binding = FragmentAboutProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        model.description.observe(getViewLifecycleOwner(), description -> {

            binding.aboutProjectDescription.setText(description);
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /* if (model == null) {
            model = new ViewModelProvider(this).get(AboutProjectViewModel.class);
        }*/
    }

}