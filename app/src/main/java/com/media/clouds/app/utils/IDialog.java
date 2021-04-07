package com.media.clouds.app.utils;

import android.content.DialogInterface;

public interface IDialog {
    void onClickPositiveButton(DialogInterface di, int which);
    void onClickNegativeButton(DialogInterface di, int which);
}
