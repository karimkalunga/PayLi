package com.media.clouds.app.features.media.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.media.clouds.app.R;
import com.media.clouds.app.databinding.VideoFragmentLayoutBinding;
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

public class VideoFragment extends Fragment implements INetworkStatus {

    private ArrayList<Map<String, String>> videos = new ArrayList<>();
    private VideoFragmentLayoutBinding binding;
    private VideoContentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = VideoFragmentLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();
    }

    /**
     * Initializes audio fragment recycler view.
     */
    private void initUI() {
        int spanCount = 3;
        GridLayoutManager lm = new GridLayoutManager(getContext(), spanCount);
        Decorator decorator = new Decorator(16, getContext(), spanCount);
        RecyclerView rv = binding.container.recyclerView;
        rv.addItemDecoration(decorator);
        rv.setLayoutManager(lm);

        adapter = new VideoContentAdapter(getActivity());
        adapter.setData(videos);
        rv.setAdapter(adapter);

        getVideo();
    }

    /**
     * Handles network request to get audio contents.
     */
    private void getVideo() {
        hideNoContentView();
        hideRetryView();
        showShimmer();
        NetworkRequest request = new NetworkRequest(this, getContext());
        request.call("get_video_content", URLConstants.VIDEO_CONTENTS_URL, null);
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
     * Display retry text on main UI when network request fails.
     */
    private void showRetryView() {
        Button tapToRetry = binding.container.tapToRetry;
        if (tapToRetry.getVisibility() == View.GONE) {
            tapToRetry.setVisibility(View.VISIBLE);

            if (!tapToRetry.hasOnClickListeners()) {
                tapToRetry.setOnClickListener(v -> getVideo());
            }
        }
    }

    /**
     * Hides retry text on main UI.
     */
    private void hideRetryView() {
        Button tapToRetry = binding.container.tapToRetry;
        if (tapToRetry.getVisibility() == View.VISIBLE) {
            tapToRetry.setVisibility(View.GONE);
        }
    }

    /**
     * Display no content text on main UI.
     */
    private void showNoContentView() {
        TextView noContentView = binding.container.noRecord;
        if (noContentView.getVisibility() == View.GONE) {
            noContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides no content text from main UI.
     */
    private void hideNoContentView() {
        TextView noContentView = binding.container.noRecord;
        if (noContentView.getVisibility() == View.VISIBLE) {
            noContentView.setVisibility(View.GONE);
        }
    }

    /**
     * Handles response from network request.
     * @param response audio data JSONArray.
     * @throws Exception JSON Exception.
     */
    private void handleVideoResponse(JSONArray response) throws Exception {
        if (!videos.isEmpty()) {
            videos.clear();
        }
        for (int i = 0; i < response.length(); i++) {
            JSONObject data = response.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            map.put(KeyConstants.ID, data.getString(KeyConstants.ID));
            map.put(KeyConstants.NAME, data.getString(KeyConstants.NAME));
            map.put(KeyConstants.CONTENT_PRICE, data.getString(KeyConstants.CONTENT_PRICE));
            map.put(KeyConstants.CONTENT_LINK, data.getString(KeyConstants.CONTENT_LINK));
            map.put(KeyConstants.CREATOR_NAME, data.getString(KeyConstants.CREATOR_NAME));
            map.put(KeyConstants.BANNER_LINK, data.getString(KeyConstants.BANNER_LINK));
            videos.add(map);
        }
        adapter.setData(videos);
    }

    @Override
    public void notifySuccess(String tag, String response) {
        hideShimmer();
        try {
            JSONArray rsp = new JSONArray(response);
            if (rsp.length() > 0) {
                handleVideoResponse(rsp);
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
