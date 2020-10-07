package com.media.clouds.app.features.entry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.OnBoardingLayoutBinding;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;

/**
 * OnBoard.class
 *
 * This class handles displaying of on-boarding screens and relating functionality.
 */
public class OnBoard extends AppCompatActivity implements DataPasser {

    public static final int NUM_PAGES = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences prefs = Preferences.getInstance(this);
        boolean hasPassed = prefs.getOnBoardingStatus();
        if (hasPassed) {
            boolean hasLoggedIn = prefs.getLoginStatus();
            if (hasLoggedIn) {
                goToHome();
            } else {
                goToSignIn();
            }
        } else {
            OnBoardingLayoutBinding binding = OnBoardingLayoutBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            binding.pager.setAdapter(new ViewPagerAdapter(this));
            new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {}).attach();
        }
    }

    /**
     * Switches to login activity.
     */
    private void goToSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Switches to home activity.
     */
    private void goToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void notifyDataPassed(String data) {
        Preferences.getInstance(this).setOnBoardingStatus(true);
        if (data.equals(OnBoardingPageFragment.SIGN_UP_CLICKED)) {
            goToSignUp();
        } else {
            goToSignIn();
        }
    }

    @Override
    public void notifyBottomSheetFragmentCreated(View view) {}

    /**
     * On-boarding view pager adapter.
     */
    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        @NonNull
        public Fragment createFragment(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt(KeyConstants.POSITION, position);
            Fragment fragment = new OnBoardingPageFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    /**
     * Switches to create account activity.
     */
    private void goToSignUp() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}
