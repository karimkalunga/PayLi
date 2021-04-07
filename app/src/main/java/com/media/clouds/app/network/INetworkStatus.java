package com.media.clouds.app.network;

import com.android.volley.VolleyError;

public interface INetworkStatus {
    void notifySuccess(String tag, String response);
    void notifyError(String tag, VolleyError error);
}
