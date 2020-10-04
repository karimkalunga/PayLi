package com.media.clouds.app.features.entry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.HomeLayoutBinding;
import com.media.clouds.app.features.media.audio.AudioFragment;
import com.media.clouds.app.features.media.utils.MediaPlaybackImpl;
import com.media.clouds.app.features.media.library.LibraryFragment;
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

    private HomeLayoutBinding binding;
    MediaPlaybackImpl playback = null;
    private View audioPlaybackView;

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
        View view = getLayoutInflater().inflate(
                R.layout.profile_zero_info_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button updateProfileButton = view.findViewById(R.id.edit_profile);
        updateProfileButton.setOnClickListener(v -> {
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

    private void handleVideoPlayback(String videoContent) {

    }

    private void handleAudioPlayback(String audioContent) throws Exception {
        showAudioPlaybackView();
        if (playback != null) {
            playback.releasePlayer();
        }
        playback = MediaPlaybackImpl.init(audioPlaybackView, audioContent);
        playback.prepareAndPlay();
    }

    @Override
    public void notifyDataPassed(String data) throws Exception {
        JSONObject passedData = new JSONObject(data);
        String tag = passedData.getString(KeyConstants.TAG);

        if (KeyConstants.AUDIO_CLICKED.equals(tag)) {
            handleAudioPlayback(data);
        } else {
            handleVideoPlayback(data);
        }
    }

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
            if (playback != null) {
                playback.releasePlayer();
            }
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

    @Override
    protected void onDestroy() {
        playback.releasePlayer();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideAudioPlaybackView();
        playback.releasePlayer();
    }
}
