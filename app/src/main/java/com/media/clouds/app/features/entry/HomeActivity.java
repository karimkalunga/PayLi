package com.media.clouds.app.features.entry;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.HomeLayoutBinding;
import com.media.clouds.app.databinding.ProfileZeroInfoBottomSheetBinding;
import com.media.clouds.app.features.media.audio.AudioFragment;
import com.media.clouds.app.features.media.library.LibraryFragment;
import com.media.clouds.app.features.media.utils.MediaPlaybackImpl;
import com.media.clouds.app.features.media.video.VideoFragment;
import com.media.clouds.app.features.profile.ProfileActivity;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * HomeActivity.class
 * This class is the landing page to our app.
 */
public class HomeActivity extends AppCompatActivity implements DataPasser {

    private static final String FLAG_CANCEL = "cancel";
    private MediaPlaybackImpl playback = null;
    private HomeLayoutBinding binding;
    private View audioPlaybackView;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        initUI();
    }

    /**
     * Switches to profile activity.
     * @param hasZeroInfo true if profile contains no information else false.
     */
    private void showProfile(boolean hasZeroInfo) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(KeyConstants.PROFILE_INFO_COUNT, hasZeroInfo);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_stay);
    }

    /**
     * Opens feedback bottom sheet.
     */
    @SuppressLint("InflateParams")
    private void showBottomSheet() {
        ProfileZeroInfoBottomSheetBinding binding =
                ProfileZeroInfoBottomSheetBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(binding.getRoot());
        dialog.show();

        binding.editProfile.setOnClickListener(v -> {
            showProfile(true);
            dialog.dismiss();
        });
    }

    /**
     * Prepares switch to profile activity.
     */
    private void goToProfile() {
        int savedInfoCount = Preferences.getInstance(this)
                .getProfileInfoCount();
        if (savedInfoCount == 0) {
            showBottomSheet();
        } else {
            showProfile(false);
        }
    }

    /**
     * Initializes and implements user profile functionality.
     */
    private void setUpProfileView() {
        CircleImageView profileImageView = binding.profile;
        String url = Preferences.getInstance(this).getProfilePhotoUrl();
        try {
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_baseline_account_circle)
                    .error(R.drawable.ic_baseline_account_circle)
                    .centerCrop()
                    .into(profileImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        profileImageView.setOnClickListener(v -> goToProfile());
    }

    /**
     * Initializes UI components such as setting listeners,
     * decorating view etc.
     */
    private void initUI() {
        audioPlaybackView = binding.playbackContainer.audioPlaybackView;

        BottomNavigationView bottomNavigation= binding.bottomNavigation;
        BottomNavigationItemSelectedListener listener = new BottomNavigationItemSelectedListener();
        bottomNavigation.setOnNavigationItemReselectedListener(listener);
        bottomNavigation.setOnNavigationItemSelectedListener(listener);
        bottomNavigation.setSelectedItemId(R.id.nav_audio);

        setUpProfileView();
    }

    /**
     * Sets view title.
     * @param title view title text.
     */
    private void setPageTitle(String title) {
        binding.title.setText(title);
    }

    /**
     * Shows audio playback view.
     */
    private void showAudioPlaybackView() {
        if (audioPlaybackView.getVisibility() == View.GONE) {
            audioPlaybackView.setVisibility(View.VISIBLE);
            audioPlaybackView.findViewById(R.id.buy_content).setVisibility(View.VISIBLE);

            TextView contentPriceTv = audioPlaybackView.findViewById(R.id.content_price);
            contentPriceTv.setVisibility(View.VISIBLE);
            contentPriceTv.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            contentPriceTv.setBackgroundResource(R.drawable.button_primary_dark_stoke_accent);
        }
    }

    /**
     * Hides audio playback view.
     */
    private void hideAudioPlaybackView() {
        if (audioPlaybackView.getVisibility() == View.VISIBLE) {
            audioPlaybackView.setVisibility(View.GONE);
        }
    }

    /**
     * Shows video playback view - bottom sheet.
     */
    private void showVideoPlaybackView() {
        releasePlayback();
        hideAudioPlaybackView();
        BottomSheetFragment dialog = BottomSheetFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "Video Player Dialog Fragment");
    }

    /**
     * Triggers release playback.
     */
    private void releasePlayback() {
        if (playback != null) {
            playback.releasePlayer();
        }
    }

    /**
     * Initiates and handles video playback.
     */
    private void handleVideoPlayback() {
        showVideoPlaybackView();
        releasePlayback();
    }

    /**
     * Initiates and handles audio playback.
     */
    private void handleAudioPlayback() {
        showAudioPlaybackView();
        releasePlayback();
        try {
            playback = MediaPlaybackImpl.init(audioPlaybackView, content, true);
            playback.prepareAndPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyDataPassed(String data) {
        if (FLAG_CANCEL.equals(data)) {
            releasePlayback();
        } else {
            this.content = data;
            try {
                JSONObject passedData = new JSONObject(content);
                String tag = passedData.getString(KeyConstants.TAG);

                if (KeyConstants.AUDIO_CLICKED.equals(tag)) {
                    handleAudioPlayback();
                } else {
                    handleVideoPlayback();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notifyBottomSheetFragmentCreated(View view) {
        try {
            playback = MediaPlaybackImpl.init(view, content, false);
            playback.prepareAndPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        releasePlayback();
        super.onDestroy();
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        hideAudioPlaybackView();
        releasePlayback();
    }*/

    /**
     * BottomNavigationItemSelectedListener.class
     * This class listens to bottom sheet navigation menu clicks.
     */
    private class BottomNavigationItemSelectedListener implements
            BottomNavigationView.OnNavigationItemReselectedListener,
            BottomNavigationView.OnNavigationItemSelectedListener {
        /**
         * Handles clicks on bottom navigation menu item.
         * @param itemId menu item ID.
         */
        private void handleNavItemClick(int itemId) {
            releasePlayback();
            hideAudioPlaybackView();

            if (R.id.nav_audio == itemId) {
                setPageTitle(getString(R.string.menu_audio));
                switchToFragment(new AudioFragment());

            } else if (R.id.nav_video == itemId) {
                setPageTitle(getString(R.string.menu_video));
                switchToFragment(new VideoFragment());

            } else if (R.id.nav_library == itemId) {
                setPageTitle(getString(R.string.menu_my_library));
                switchToFragment(new LibraryFragment());
            }
        }

        /**
         * Handles switch between fragments.
         * @param fragment to switch to.
         */
        private void switchToFragment(Fragment fragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.container.getId(), fragment)
                    .commit();
        }

        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            handleNavItemClick(item.getItemId());
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            handleNavItemClick(item.getItemId());
            return true;
        }
    }

    /**
     * Handles creation of video playback bottom sheet.
     */
    public static class BottomSheetFragment extends BottomSheetDialogFragment {
        private DataPasser passer;
        private BottomSheetFragment() {}

        /**
         * Singleton.
         * @return bottom sheet fragment instance.
         */
        public static BottomSheetFragment newInstance() {
            return new BottomSheetFragment();
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            passer = (DataPasser) context;
        }

        @Override
        public void setupDialog(@NonNull Dialog dialog, int style) {
            View contentView = View.inflate(getContext(), R.layout.video_playback_layout, null);
            dialog.setContentView(contentView);
            ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

            passer.notifyBottomSheetFragmentCreated(contentView);
        }

        @Override
        public void onDestroy() {
            passer.notifyDataPassed(FLAG_CANCEL);
            super.onDestroy();
        }
    }
}
