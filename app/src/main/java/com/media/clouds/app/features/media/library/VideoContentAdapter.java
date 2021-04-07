package com.media.clouds.app.features.media.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.media.clouds.app.databinding.LibraryVideoContentLayoutBinding;
import com.media.clouds.app.features.media.utils.ContentDataLayer;
import com.media.clouds.app.utils.DataPasser;
import com.media.clouds.app.utils.KeyConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class VideoContentAdapter extends Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Map<String, String>> videos = new ArrayList<>();
    private boolean isContentOffline;
    private DataPasser passer;
    private Context context;

    /**
     * Constructor.
     * @param context application context.
     */
    public VideoContentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        passer = (DataPasser) context;
    }

    /**
     * Initializes array of video contents.
     * @param videos list of video content.
     */
    public void setData(ArrayList<Map<String, String>> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    /**
     * Handles click on video content.
     * @param position adapter position.
     */
    private void handleVideoContentClick(int position) {
        try {
            JSONObject data = new JSONObject(videos.get(position));
            data.put(KeyConstants.TAG, KeyConstants.VIDEO_CLICKED);
            data.put(KeyConstants.IS_BOUGHT, true);
            data.put(KeyConstants.IS_DOWNLOADED, isContentOffline);
            passer.notifyDataPassed(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This class handles actions performed on inflated video layout.
     */
    public class ContentViewHolder extends RecyclerView.ViewHolder {
        private LibraryVideoContentLayoutBinding binding;

        /**
         * Constructor.
         * @param binding video content view binding.
         */
        public ContentViewHolder(LibraryVideoContentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.container.setOnClickListener(
                    v -> handleVideoContentClick(getAdapterPosition()));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContentViewHolder(
                LibraryVideoContentLayoutBinding.inflate(inflater, parent, false));
    }

    /**
     * Binds data to view - video content inflated view.
     * @param holder video content view holder.
     * @param position data index/position.
     */
    private void bindVideoContent(ContentViewHolder holder, int position) throws Exception {
        JSONObject obj = new JSONObject(videos.get(position));
        ContentDataLayer dataHolder = ContentDataLayer.init(obj.toString());
        Picasso.get()
                .load(dataHolder.getBannerLink(context))
                .into(holder.binding.contentImg);

        // todo: Check if content is downloaded and update isOnlineContent status.
        isContentOffline = false;
        if (isContentOffline) {
            AudioContentAdapter.setOfflineContentView(holder.binding.contentPrice);
        } else {
            AudioContentAdapter.setOnlineContentView(holder.binding.contentPrice);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            bindVideoContent((ContentViewHolder) holder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return videos != null ? videos.size() : 0;
    }
}
