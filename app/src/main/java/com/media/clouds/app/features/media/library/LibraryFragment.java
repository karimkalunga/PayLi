package com.media.clouds.app.features.media.library;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.media.clouds.app.R;
import com.media.clouds.app.databinding.LibraryFragmentLayoutBinding;
import com.media.clouds.app.network.INetworkStatus;
import com.media.clouds.app.network.NetworkRequest;
import com.media.clouds.app.utils.Decorator;
import com.media.clouds.app.utils.DialogImpl;
import com.media.clouds.app.utils.KeyConstants;
import com.media.clouds.app.utils.URLConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryFragment extends Fragment implements INetworkStatus {

    private ArrayList<Map<String, String>> videos = new ArrayList<>();
    private ArrayList<Map<String, String>> audios = new ArrayList<>();
    private LibraryFragmentLayoutBinding binding;
    private VideoContentAdapter videoAdapter;
    private AudioContentAdapter audioAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LibraryFragmentLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initAudioRecyclerView();
        initVideoRecyclerView();
        getLibraryContent();
    }

    /**
     * Initializes library (video) fragment recycler view.
     */
    private void initVideoRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        Decorator decorator = new Decorator(16, getContext(), Decorator.VERTICAL);
        RecyclerView rv = binding.video.recyclerView;
        rv.addItemDecoration(decorator);
        rv.setLayoutManager(llm);

        videoAdapter = new VideoContentAdapter(getActivity());
        videoAdapter.setData(videos);
        rv.setAdapter(videoAdapter);
    }

    /**
     * Initializes library (audio) fragment recycler view.
     */
    private void initAudioRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        Decorator decorator = new Decorator(16, getContext(), Decorator.HORIZONTAL);
        RecyclerView rv = binding.audio.recyclerView;
        rv.setLayoutManager(llm);
        rv.addItemDecoration(decorator);

        audioAdapter = new AudioContentAdapter(getActivity());
        audioAdapter.setData(audios);
        rv.setAdapter(audioAdapter);
    }

    /**
     * Handles network request to get library contents.
     */
    private void getLibraryContent() {
        hideNoContentView();
        hideRetryView();
        showShimmer();
        NetworkRequest request = new NetworkRequest(this, getContext());
        request.call("get_library_content", URLConstants.LIBRARY_CONTENTS_URL, null);
    }

    /**
     * Display loading UI with shimmering effect.
     */
    private void showShimmer() {
        ShimmerFrameLayout shimmer = binding.shimmerViewContainer;
        if (shimmer.getVisibility() == View.GONE) {
            shimmer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides shimmering effect layout.
     */
    private void hideShimmer() {
        ShimmerFrameLayout shimmer = binding.shimmerViewContainer;
        if (shimmer.getVisibility() == View.VISIBLE) {
            shimmer.setVisibility(View.GONE);
        }
    }

    /**
     * Display video section in main UI.
     */
    private void showVideoSection() {
        LinearLayout videoLL = binding.videoContainer;
        if (videoLL.getVisibility() == View.GONE) {
            videoLL.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides video section from main UI.
     */
    private void hideVideoSection() {
        LinearLayout videoLL = binding.videoContainer;
        if (videoLL.getVisibility() == View.VISIBLE) {
            videoLL.setVisibility(View.GONE);
        }
    }

    /**
     * Display audio section in main UI.
     */
    private void showAudioSection() {
        LinearLayout audioLL = binding.audioContainer;
        if (audioLL.getVisibility() == View.GONE) {
            audioLL.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides audio section from main UI.
     */
    private void hideAudioSection() {
        LinearLayout audioLL = binding.audioContainer;
        if (audioLL.getVisibility() == View.VISIBLE) {
            audioLL.setVisibility(View.GONE);
        }
    }

    /**
     * Display retry text on main UI when network request fails.
     */
    private void showRetryView() {
        Button tapToRetry = binding.tapToRetry;
        if (tapToRetry.getVisibility() == View.GONE) {
            tapToRetry.setVisibility(View.VISIBLE);

            if (!tapToRetry.hasOnClickListeners()) {
                tapToRetry.setOnClickListener(v -> getLibraryContent());
            }
        }
    }

    /**
     * Hides retry text on main UI.
     */
    private void hideRetryView() {
        Button tapToRetry = binding.tapToRetry;
        if (tapToRetry.getVisibility() == View.VISIBLE) {
            tapToRetry.setVisibility(View.GONE);
        }
    }

    /**
     * Display no content text on main UI.
     */
    private void showNoContentView() {
        TextView noContentView = binding.noRecord;
        if (noContentView.getVisibility() == View.GONE) {
            noContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides no content text from main UI.
     */
    private void hideNoContentView() {
        TextView noContentView = binding.noRecord;
        if (noContentView.getVisibility() == View.VISIBLE) {
            noContentView.setVisibility(View.GONE);
        }
    }

    /**
     * Gets audio & video mapped data from JSON object.
     * @param data audio/video content.
     * @return mapped array of audio/video content.
     * @throws Exception JSONException
     */
    private Map<String, String> getContentMap(JSONObject data) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put(KeyConstants.ID, data.getString(KeyConstants.ID));
        map.put(KeyConstants.NAME, data.getString(KeyConstants.NAME));
        map.put(KeyConstants.CONTENT_PRICE, data.getString(KeyConstants.CONTENT_PRICE));
        map.put(KeyConstants.CONTENT_LINK, data.getString(KeyConstants.CONTENT_LINK));
        map.put(KeyConstants.CREATOR_NAME, data.getString(KeyConstants.CREATOR_NAME));
        map.put(KeyConstants.BANNER_LINK, data.getString(KeyConstants.BANNER_LINK));

        return map;
    }

    /**
     * Works on video data return from network call.
     * @param response video data.
     * @throws Exception JSONException
     */
    private void handleVideoContent(JSONArray response) throws Exception {
        int size = response.length();
        if (size > 0) {
            showVideoSection();
            for (int i = 0; i < response.length(); i++) {
                Map<String, String> map = getContentMap(response.getJSONObject(i));
                videos.add(map);
            }
            videoAdapter.setData(videos);
        } else {
            hideVideoSection();
        }
    }

    /**
     * Works on audio data return from network call.
     * @param response audio data.
     * @throws Exception JSONException
     */
    private void handleAudioContent(JSONArray response) throws Exception {
        int size = response.length();
        if (size > 0) {
            showAudioSection();
            for (int i = 0; i < response.length(); i++) {
                Map<String, String> map = getContentMap(response.getJSONObject(i));
                audios.add(map);
            }
            audioAdapter.setData(audios);
        } else {
            hideAudioSection();
        }
    }

    /**
     * Handles response from network request.
     * @param response audio data JSONArray.
     * @throws Exception JSON Exception.
     */
    private void handleLibraryResponse(JSONObject response) throws Exception {
        if (!videos.isEmpty()) {
            videos.clear();
        }
        if (!audios.isEmpty()) {
            audios.clear();
        }
        handleVideoContent(response.getJSONArray(KeyConstants.VIDEO));
        handleAudioContent(response.getJSONArray(KeyConstants.AUDIO));
    }

    @Override
    public void notifySuccess(String tag, String response) {
        hideShimmer();
        try {
            if (response != null && !TextUtils.isEmpty(response)) {
                JSONObject rsp = new JSONObject(response);
                handleLibraryResponse(rsp);
            } else {
                showNoContentView();
            }
        } catch (Exception e) {
            showRetryView();
            DialogImpl.getInstance(getContext())
                    .showAlertDialog(getString(R.string.network_error));
            e.printStackTrace();
        }
    }

    @Override
    public void notifyError(String tag, VolleyError error) {
        hideShimmer();
        showRetryView();
        DialogImpl.getInstance(getContext())
                .showAlertDialog(getString(R.string.network_error));
        error.printStackTrace();
    }
}
