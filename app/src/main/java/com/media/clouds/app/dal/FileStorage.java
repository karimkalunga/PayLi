package com.media.clouds.app.dal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Objects;

public class FileStorage {
    private Context context;

    public FileStorage(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    public File getPrivateStorageDir(String id, String tag) {
        String dir;
        switch (Objects.requireNonNull(tag)) {
            case "video":
                dir = Environment.DIRECTORY_MOVIES;
                break;
            case "audio":
                dir = Environment.DIRECTORY_MUSIC;
                break;
            default:
                dir = Environment.DIRECTORY_DOCUMENTS;
                break;
        }
        File file = new File(context.getExternalFilesDir(dir), id);
        if (! file.mkdirs()) {
            Log.d("XXX_FILE", "Directory not created");
        }
        return file;
    }
}
