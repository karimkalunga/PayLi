package com.media.clouds.app.features.media;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.media.clouds.app.R;
import com.google.android.exoplayer2.ui.PlayerView;

public class FullscreenVideoActivity extends AppCompatActivity {

   /* private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mHideRunnable = this::hide;

    private String sStatus;
    private String[] mVideoData;
    private boolean isPurchased;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_layout);
        mContentView = findViewById(R.id.enclosing_layout);

        if (getIntent() != null) {
            mVideoData = getIntent().getStringArrayExtra(ExoPlayerViewManager.EXTRA_VIDEO_URI);
            isPurchased = getIntent().getBooleanExtra(
                    ExoPlayerViewManager.EXTRA_VIDEO_PURCHASED, false);
            sStatus = getIntent().getStringExtra(ExoPlayerViewManager.EXTRA_S_STATUS);
        }

        PlayerView playerView = findViewById(R.id.playerView);
        ExoPlayerViewManager.getInstance(mVideoData, isPurchased, sStatus)
                .prepareExoPlayer(this, playerView);

        View controlView = playerView.findViewById(R.id.exo_controller);
        ImageView fullscreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenIcon.setImageResource(R.drawable.exo_controls_fullscreen_exit);

        controlView.findViewById(R.id.exo_fullscreen_button)
                .setOnClickListener(v -> finish());
    }

    @Override
    public void onResume() {
        super.onResume();
        ExoPlayerViewManager.getInstance(mVideoData, isPurchased, sStatus)
                .goToForeground();
    }

    @Override
    public void onPause() {
        super.onPause();
        ExoPlayerViewManager.getInstance(mVideoData, isPurchased, sStatus)
                .goToBackground();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide();
    }

    private void hide() {
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 100);
    }*/
}
