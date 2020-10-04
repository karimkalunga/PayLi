package com.media.clouds.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.media.clouds.app.utils.KeyConstants;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * NetworkRequest.class
 * This class handles request and response from remote server.
 */
public class NetworkRequest {

    private final String KEY = "cm!@098";
    private INetworkStatus callback;
    private Context context;

    /**
     * Constructor.
     * @param callback network response status.
     * @param context application context.
     */
    public NetworkRequest(INetworkStatus callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    /**
     * Checks whether internet connectivity is available
     * during network request.
     * @return true/false.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = (connectivityManager != null)
                ? connectivityManager.getActiveNetworkInfo()
                : null;
        return activeNetworkInfo != null;
    }

    /**
     * Sends request to the server.
     * @param tag request type.
     * @param url request url.
     * @param param request parameters.
     */
    public void call(String tag, String url, JSONObject param) {
        try {
            boolean connected = isNetworkAvailable();
            if (connected) {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                    if (callback != null) {
                        callback.notifySuccess(tag, response);
                    }
                }, error -> {
                    if (callback != null) {
                        callback.notifyError(tag, error);
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        if (param == null) {
                            return null;
                        }
                        return param.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put(KeyConstants.API_KEY, KEY);
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
            else {
                throw new VolleyError(KeyConstants.NO_CONNECTIVITY);
            }
        }
        catch(VolleyError e) {
            callback.notifyError(tag, e);
        }
    }
}
