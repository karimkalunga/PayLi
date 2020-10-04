package com.media.clouds.app.features.media.audio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.media.clouds.app.R;
import com.media.clouds.app.databinding.AudioContentLayoutBinding;
import com.media.clouds.app.databinding.AudioPromotedContentLayoutBinding;
import com.media.clouds.app.features.media.utils.ContentDataLayer;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class AudioContentAdapter extends Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Map<String, String>> audios = new ArrayList<>();
    private ContentDataLayer dataHolder;
    private DataPasser passer;
    private Context context;

    /**
     * Constructor.
     * @param context application context.
     */
    public AudioContentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        passer = (DataPasser) context;
    }

    /**
     * Initializes array of audio contents.
     * @param audios list of audio content.
     */
    public void setData(ArrayList<Map<String, String>> audios) {
        this.audios = audios;
        notifyDataSetChanged();
    }

    /**
     * Handles click on audio content.
     * @param position adapter position.
     */
    private void handleAudioContentClick(int position) {
        try {
            JSONObject data = new JSONObject(audios.get(position));
            data.put(KeyConstants.TAG, KeyConstants.AUDIO_CLICKED);
            data.put(KeyConstants.IS_BOUGHT, false);
            passer.notifyDataPassed(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This class handles actions performed on inflated audio layout.
     */
    public class ContentViewHolder extends RecyclerView.ViewHolder {
        private AudioContentLayoutBinding binding;

        /**
         * Constructor.
         * @param binding audio content view binding.
         */
        public ContentViewHolder(AudioContentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.container.setOnClickListener(
                    v -> handleAudioContentClick(getAdapterPosition()));
        }
    }

    /**
     * This class handles actions performed on inflated promoted audio layout.
     */
    public class PromotedContentViewHolder extends RecyclerView.ViewHolder {
        private AudioPromotedContentLayoutBinding binding;

        /**
         * Constructor.
         * @param binding promoted audio content view binding.
         */
        public PromotedContentViewHolder(AudioPromotedContentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.container.setOnClickListener(
                    v -> handleAudioContentClick(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 2) {
            return new PromotedContentViewHolder(
                    AudioPromotedContentLayoutBinding.inflate(inflater, parent, false));
        } else {
            return new ContentViewHolder(
                    AudioContentLayoutBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * Binds data to view - promoted audio content inflated view.
     * @param holder promoted audio content view holder.
     * @param position data index/position.
     */
    private void bindPromotedAudioContent(PromotedContentViewHolder holder, int position) throws Exception {
        JSONObject obj = new JSONObject(audios.get(position));
        dataHolder = ContentDataLayer.init(obj.toString());
        String price = context.getString(R.string.currency)
                .concat(" ")
                .concat(dataHolder.getPrice());

        holder.binding.contentName.setText(dataHolder.getTitle());
        holder.binding.artistName.setText(dataHolder.getAuthorName());
        holder.binding.contentPrice.setText(price);
        Picasso.get()
                .load(dataHolder.getBannerLink(context))
                .into(holder.binding.contentImg);
    }

    /**
     * Binds data to view - audio content inflated view.
     * @param holder audio content view holder.
     * @param position data index/position.
     */
    private void bindAudioContent(ContentViewHolder holder, int position) throws Exception {
        JSONObject obj = new JSONObject(audios.get(position));
        dataHolder = ContentDataLayer.init(obj.toString());
        String price = context.getString(R.string.currency)
                .concat(" ")
                .concat(dataHolder.getPrice());

        holder.binding.contentName.setText(dataHolder.getTitle());
        holder.binding.artistName.setText(dataHolder.getAuthorName());
        holder.binding.contentPrice.setText(price);
        Picasso.get()
                .load(dataHolder.getBannerLink(context))
                .into(holder.binding.contentImg);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (position == 2) {
                bindPromotedAudioContent((PromotedContentViewHolder) holder, position);
            } else {
                bindAudioContent((ContentViewHolder) holder, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return audios != null ? audios.size() : 0;
    }
}
