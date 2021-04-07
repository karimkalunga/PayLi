package com.media.clouds.app.features.entry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.media.clouds.app.databinding.OnBoardingTaglineLayoutBinding;

public class RegistrationOnBoardingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        OnBoardingTaglineLayoutBinding binding =
                OnBoardingTaglineLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
