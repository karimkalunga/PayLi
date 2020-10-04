package com.media.clouds.app.features.media.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.media.clouds.app.R;
import com.media.clouds.app.dal.Preferences;
import com.media.clouds.app.databinding.AudioPlaybackLayoutBinding;
import com.media.clouds.app.features.media.payment.ContentPurchaseImpl;
import com.squareup.picasso.Picasso;

/**
 * AudioPlaybackImpl.class
 *
 * This class handles displaying of audio playback view and
 * initiates playback of audio contents.
 */
public class MediaPlaybackImpl implements IEventListener {

    private AudioPlaybackLayoutBinding binding;
    private ContentDataLayer dataHolder;
    private MediaPlayerImpl mediaPlayer;
    private String content, contentUrl;
    private boolean isBought;
    private Context context;

    /**
     * Initiates audio content playback.
     */
    public void prepareAndPlay() {
        mediaPlayer.initAndPlay(contentUrl, isBought, true);
    }

    /**
     * Releases the content player.
     */
    public void releasePlayer() {
        mediaPlayer.releasePlayer();
    }

    /**
     * Adds audio content data to playback view.
     * @throws Exception JSON Exception.
     */
    private void bindDataToUI() throws Exception {
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
     */
    private void hidePurchaseButton() {
        Button purchaseButton = binding.buyContent;
        if (purchaseButton.getVisibility() == View.VISIBLE) {
            purchaseButton.setVisibility(View.GONE);
        }
    }

    /**
     * Handles purchase of audio content.
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
     * Initializes UI components such as setting listeners,
     * decorating view etc.
     */
    private void initUI() throws Exception {
        dataHolder = ContentDataLayer.init(content);
        isBought = dataHolder.getPurchaseStatus();

        if (isBought) {
            hidePurchaseButton();
        }
        binding.buyContent.setOnClickListener(v -> handlePurchase());
        bindDataToUI();
    }

    /**
     * Constructor.
     * @param view audio playback inflated view.
     * @param audioContent audio data.
     * @throws Exception JSON Exception.
     */
    private MediaPlaybackImpl(View view, String audioContent) throws Exception {
        binding = AudioPlaybackLayoutBinding.bind(view);
        this.content = audioContent;
        this.context = binding.getRoot().getContext();
        mediaPlayer = MediaPlayerImpl.getInstance(
                this, context, binding.customAudioPlaybackView.mediaController);

        initUI();
    }

    /**
     * Singleton.
     * @param view audio playback inflated view.
     * @param audioContent audio data.
     * @return AudioPlaybackImpl instance.
     * @throws Exception JSON Exception.
     */
    public static synchronized MediaPlaybackImpl init(View view, String audioContent) throws Exception {
        return new MediaPlaybackImpl(view, audioContent);
    }

    @Override
    public void onPlaybackStateChanged(int state) {}

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {}

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
}
