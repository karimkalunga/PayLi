package com.media.clouds.app.features.media.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.media.clouds.app.R;
import com.media.clouds.app.databinding.AudioContentLayoutBinding;
import com.media.clouds.app.features.media.utils.ContentDataLayer;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class AudioContentAdapter extends Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Map<String, String>> audios = new ArrayList<>();
    private boolean isContentOffline;
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
            data.put(KeyConstants.IS_BOUGHT, true);
            data.put(KeyConstants.IS_DOWNLOADED, isContentOffline);
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
     * Sets online badge on text view.
     * @param view to be updated.
     */
    public static void setOnlineContentView(TextView view) {
        Context context = view.getContext();
        view.setText(context.getString(R.string.online));
        view.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
        view.setBackgroundResource(R.drawable.button_primary_dark_stoke_text_primary);
        view.setPadding(16, 6, 16, 6);
    }

    /**
     * Sets offline badge on text view.
     * @param view to be updated.
     */
    public static void setOfflineContentView(TextView view) {
        Context context = view.getContext();
        view.setText(context.getString(R.string.offline));
        view.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        view.setBackgroundResource(R.drawable.button_primary_dark_stoke_accent);
        view.setPadding(16, 6, 16, 6);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContentViewHolder(
                AudioContentLayoutBinding.inflate(inflater, parent, false));
    }

    /**
     * Binds data to view - audio content inflated view.
     * @param holder audio content view holder.
     * @param position data index/position.
     */
    private void bindAudioContent(ContentViewHolder holder, int position) throws Exception {
        JSONObject obj = new JSONObject(audios.get(position));
        ContentDataLayer dataHolder = ContentDataLayer.init(obj.toString());
        holder.binding.contentName.setText(dataHolder.getTitle());
        holder.binding.artistName.setText(dataHolder.getAuthorName());
        Picasso.get()
                .load(dataHolder.getBannerLink(context))
                .into(holder.binding.contentImg);

        // todo: Check if content is downloaded and update isOnlineContent status.
        isContentOffline = false;
        if (isContentOffline) {
            setOfflineContentView(holder.binding.contentPrice);
        } else {
            setOnlineContentView(holder.binding.contentPrice);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            bindAudioContent((ContentViewHolder) holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return audios != null ? audios.size() : 0;
    }
}
