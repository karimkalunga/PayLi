package com.media.clouds.app.utils;

import android.view.View;

public interface DataPasser {
    void notifyDataPassed(String data);
    void notifyBottomSheetFragmentCreated(View view);
}
