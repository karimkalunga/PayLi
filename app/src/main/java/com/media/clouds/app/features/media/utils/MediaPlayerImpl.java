package com.media.clouds.app.features.media.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.media.clouds.app.R;
import com.media.clouds.app.databinding.CustomAudioPlaybackViewBinding;
import com.media.clouds.app.utils.FormatterImpl;

/**
 * MediaPlayerImpl.class
 *
 * This class handles media (audio and video) playback over the
 * network or local storage.
 */
public class MediaPlayerImpl {

    private CustomAudioPlaybackViewBinding binding;
    private boolean playWhenReady = true;
    private long playbackPosition = 0;
    private boolean isPlaying = false;
    private IEventListener callback;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private Handler handler;

    /**
     * Listens to player event changes.
     */
    private class EventListenerImpl implements Player.EventListener {
        @Override
        public void onPlaybackStateChanged(int state) {
            if (state == ExoPlayer.STATE_ENDED) {
                setPlayPause(false);
                player.seekTo(0);
            } else if (state == ExoPlayer.STATE_READY) {
                setAudioProgress();
            }
            callback.onPlaybackStateChanged(state);
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            callback.onPlayWhenReadyChanged(playWhenReady, reason);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            callback.onPlayerError(error);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            callback.onPlaybackParametersChanged(playbackParameters);
        }
    }

    /**
     * Sets progress to custom audio playback view.
     */
    private void setAudioProgress() {
        SeekBar seekPlayerProgress = binding.mediaControllerProgress;
        seekPlayerProgress.setProgress(0);
        seekPlayerProgress.setMax((int) player.getDuration()/1000);

        TextView txtCurrentTime = binding.playerTimeStart;
        TextView txtEndTime = binding.playerTimeEnd;
        FormatterImpl formatter = FormatterImpl.getInstance();
        txtCurrentTime.setText(formatter.stringForTime((int) player.getCurrentPosition()));
        txtEndTime.setText(formatter.stringForTime((int) player.getDuration()));

        if (handler == null) {
            handler = new Handler();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying) {
                    seekPlayerProgress.setMax((int) player.getDuration()/1000);
                    int mCurrentPosition = (int) player.getCurrentPosition() / 1000;
                    seekPlayerProgress.setProgress(mCurrentPosition);
                    txtCurrentTime.setText(formatter.stringForTime((int) player.getCurrentPosition()));
                    txtEndTime.setText(formatter.stringForTime((int) player.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    /**
     * Handles SeekBar progress change.
     */
    private class SeekBarChangeListenerImpl implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (! fromUser) {
                return;
            }
            player.seekTo(progress*1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * Initializes audio custom playback view.
     */
    private void initCustomAudioPlaybackUI() {
        SeekBar seekPlayerProgress = binding.mediaControllerProgress;
        seekPlayerProgress.requestFocus();
        seekPlayerProgress.setOnSeekBarChangeListener(new SeekBarChangeListenerImpl());
        seekPlayerProgress.setMax(0);
        seekPlayerProgress.setMax((int) player.getDuration()/1000);
        binding.btnPlay.setOnClickListener(v -> setPlayPause(!isPlaying));
    }

    /**
     *
     * @param play audio playback status.
     */
    private void setPlayPause(boolean play) {
        isPlaying = play;
        player.setPlayWhenReady(play);
        if(!isPlaying) {
            binding.btnPlay.setImageResource(R.drawable.ic_baseline_play);
        } else{
            if (player.getPlaybackState() == Player.STATE_READY) {
                setAudioProgress();
            }
            binding.btnPlay.setImageResource(R.drawable.ic_baseline_pause);
        }
    }

    /**
     * Constructor.
     */
    private MediaPlayerImpl(IEventListener callback, Context context, View view) {
        this.callback = callback;
        player = new SimpleExoPlayer.Builder(context).build();
        player.addListener(new EventListenerImpl());
        binding = CustomAudioPlaybackViewBinding.bind(view);
    }

    /**
     * Singleton.
     * @param context application context.
     * @param playerView audio player view.
     * @return media player instance.
     */
    public static synchronized MediaPlayerImpl getInstance(
            IEventListener callback, Context context, View playerView) {
        return new MediaPlayerImpl(callback, context, playerView);
    }

    /**
     * Initializes player and begin to play content.
     * @param contentUrl media content URL.
     * @param isBought whether media is purchased or not.
     * @param isAudio whether media is audio.
     */
    public void initAndPlay(String contentUrl, boolean isBought, boolean isAudio) {
        long previewDuration = 30_000;
        MediaItem item = isBought
                ? MediaItem.fromUri(contentUrl)
                : new MediaItem.Builder()
                    .setUri(contentUrl)
                    .setClipEndPositionMs(previewDuration)
                    .build();
        player.setMediaItem(item);
        player.seekTo(currentWindow, playbackPosition + 1);
        player.prepare();

        if (isAudio) {
            initCustomAudioPlaybackUI();
            setPlayPause(true);
        } else {
            player.setPlayWhenReady(playWhenReady);
        }
    }

    /**
     * Reset media player instance.
     */
    public void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
