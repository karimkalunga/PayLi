package com.media.clouds.app.features.media.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.AudioPlaybackLayoutBinding;
import com.media.clouds.app.databinding.VideoPlaybackLayoutBinding;
import com.media.clouds.app.features.media.payment.ContentPurchaseImpl;
import com.squareup.picasso.Picasso;

/**
 * MediaPlaybackImpl.class
 *
 * This class handles displaying of audio playback view and
 * initiates playback of audio and video contents.
 */
public class MediaPlaybackImpl implements IEventListener {

    private VideoPlaybackLayoutBinding videoBinding;
    private ContentDataLayer dataHolder;
    private MediaPlayerImpl mediaPlayer;
    private String contentUrl;
    private boolean isBought, isAudio;
    private Context context;

    /**
     * Initiates audio/video content playback.
     */
    public void prepareAndPlay() {
        mediaPlayer.initAndPlay(contentUrl, isBought);
    }

    /**
     * Releases the content player.
     */
    public void releasePlayer() {
        mediaPlayer.releasePlayer();
    }

    /**
     * Adds video content data to playback view.
     * @throws Exception JSON Exception.
     * @param binding video playback binding.
     */
    private void bindDataToUI(VideoPlaybackLayoutBinding binding) throws Exception {
        contentUrl = dataHolder.getContentLink(context);
        String price = context.getString(R.string.currency)
                .concat(" ").concat(dataHolder.getPrice());

        binding.contentName.setText(dataHolder.getTitle());
        binding.contentPrice.setText(price);
        binding.artistName.setText(dataHolder.getAuthorName());
    }

    /**
     * Adds audio content data to playback view.
     * @throws Exception JSON Exception.
     * @param binding audio playback layout.
     */
    private void bindDataToUI(AudioPlaybackLayoutBinding binding) throws Exception {
        contentUrl = dataHolder.getContentLink(context);
        String price = context.getString(R.string.currency)
                .concat(" ").concat(dataHolder.getPrice());

        binding.contentName.setText(dataHolder.getTitle());
        binding.contentPrice.setText(price);
        Picasso.get()
                .load(dataHolder.getBannerLink(context))
                .into(binding.contentImg);
    }

    /**
     * Hides the purchase button.
     * @param binding video playback view.
     */
    private void hidePurchaseButton(VideoPlaybackLayoutBinding binding) {
        Button purchaseButton = binding.buyContent;
        if (purchaseButton.getVisibility() == View.VISIBLE) {
            purchaseButton.setVisibility(View.GONE);
        }
    }

    /**
     * Hides the purchase button.
     * @param binding audio playback view.
     */
    private void hidePurchaseButton(AudioPlaybackLayoutBinding binding) {
        Button purchaseButton = binding.buyContent;
        if (purchaseButton.getVisibility() == View.VISIBLE) {
            purchaseButton.setVisibility(View.GONE);
        }
    }

    /**
     * Handles purchase of audio/video content.
     */
    private void handlePurchase() {
        try {
            String userId = Preferences.getInstance(context).getUserId();
            String contentId = dataHolder.getId();
            ContentPurchaseImpl.getInstance(contentId, userId).purchase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor.
     * @param view audio/video playback inflated view.
     * @param content audio/video data.
     * @param isAudio whether content is audio or not.
     * @throws Exception JSON Exception.
     */
    private MediaPlaybackImpl(View view, String content, boolean isAudio) throws Exception {
        this.isAudio = isAudio;
        dataHolder = ContentDataLayer.init(content);
        isBought = dataHolder.getPurchaseStatus();

        if (isAudio) {
            initAudioPlaybackView(view);
        } else {
            initVideoPlaybackView(view);
        }
    }

    /**
     * Initializes video playback.
     * @param view video playback view.
     * @throws Exception JSON Exception.
     */
    private void initVideoPlaybackView(View view) throws Exception {
        videoBinding = VideoPlaybackLayoutBinding.bind(view);
        this.context = videoBinding.getRoot().getContext();
        mediaPlayer = MediaPlayerImpl.getInstance(this, context, view, false);

        if (isBought) {
            hidePurchaseButton(videoBinding);
        }
        videoBinding.buyContent.setOnClickListener(v -> handlePurchase());
        bindDataToUI(videoBinding);
    }

    /**
     * Initializes audio playback.
     * @param view audio playback view.
     * @throws Exception JSON Exception.
     */
    private void initAudioPlaybackView(View view) throws Exception {
        AudioPlaybackLayoutBinding binding = AudioPlaybackLayoutBinding.bind(view);
        this.context = binding.getRoot().getContext();
        mediaPlayer = MediaPlayerImpl.getInstance(
                this, context, binding.customAudioPlaybackView.mediaController, true);

        if (isBought) {
            hidePurchaseButton(binding);
        }
        binding.buyContent.setOnClickListener(v -> handlePurchase());
        bindDataToUI(binding);
    }

    /**
     * Singleton.
     * @param view playback inflated view.
     * @param content audio/video data.
     * @param isAudio whether content is audio or not.
     * @return MediaPlaybackImpl instance.
     * @throws Exception JSON Exception.
     */
    public static synchronized MediaPlaybackImpl init(
            View view, String content, boolean isAudio) throws Exception {
        return new MediaPlaybackImpl(view, content, isAudio);
    }

    @Override
    public void onPlaybackStateChanged(int state) {
        if (state == ExoPlayer.STATE_BUFFERING && !isAudio) {
            if (videoBinding != null) {
                videoBinding.container.progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (videoBinding != null) {
                videoBinding.container.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {}

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
}
