package com.media.clouds.app.features.media.utils;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;

public interface IEventListener {
    void onPlaybackStateChanged(int state);
    void onPlayerError(ExoPlaybackException error);
    void onPlayWhenReadyChanged(boolean playWhenReady, int reason);
    void onPlaybackParametersChanged(PlaybackParameters playbackParameters);
}
