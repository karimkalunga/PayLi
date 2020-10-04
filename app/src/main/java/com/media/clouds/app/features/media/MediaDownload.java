package com.media.clouds.app.features.media;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.media.clouds.app.R;
import com.media.clouds.app.dal.DownloadedMedia;
import com.media.clouds.app.features.media.library.DownloadsViewModel;
import com.media.clouds.app.security.EncoDecode;
import com.media.clouds.app.utils.KeyConstants;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MediaDownload {

    private Map<String, String> tmp;
    private String bookPath;
    private Context context;
    private long downloadID;
    private File baseFile;
    private DownloadManager downloadManager;

    private void encrypt(File baseFile) {
        context.registerReceiver(receiver, new IntentFilter("encryption"));
        //EncoDecode.init(context);
        try {
            //EncoDecode.encode(baseFile, context);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("status", false)) {
                DownloadsViewModel viewModel = ViewModelProvider.AndroidViewModelFactory
                        .getInstance((Application) context.getApplicationContext())
                        .create(DownloadsViewModel.class);

                viewModel.insert(new DownloadedMedia(
                        Integer.parseInt(Objects.requireNonNull(tmp.get(KeyConstants.ID))),
                        tmp.get(KeyConstants.TITLE),
                        tmp.get(KeyConstants.NAME),
                        tmp.get(KeyConstants.AMOUNT),
                        tmp.get(KeyConstants.MEDIA_TAG),
                        tmp.get(KeyConstants.DESCRIPTION),
                        bookPath,
                        tmp.get(KeyConstants.BANNER_LINK),
                        tmp.get(KeyConstants.PURCHASE_START),
                        tmp.get(KeyConstants.PURCHASE_END)
                ));
                //EncoDecode.shutDown();
            }
        }
    };

    public MediaDownload(Context context) {
        this.context = context;
        BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    boolean isCancelled = checkCancelStatus(id);

                    if (!isCancelled) {
                        Toast.makeText(
                                context.getApplicationContext(),
                                context.getString(R.string.download_complete),
                                Toast.LENGTH_LONG
                        ).show();

                        encrypt(baseFile);
                    }
                    else {
                        Toast.makeText(
                                context.getApplicationContext(),
                                context.getString(R.string.download_cancelled),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            }
        };
        context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private boolean checkCancelStatus(long id) {
        DownloadManager.Query mDownloadQuery = new DownloadManager.Query();
        mDownloadQuery.setFilterById(id);
        Cursor cursor = downloadManager.query(mDownloadQuery);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status;
        try {
            status = cursor.getInt(columnIndex);
            Log.d("download status = ", String.valueOf(status));
        }
        catch (CursorIndexOutOfBoundsException e) {
            return true;
        }
        return false;
    }

    public void downLoadMedia(Map<String, String> tmp) {
        this.tmp = tmp;
        addDownload(tmp);
    }

    private void addDownload(Map<String, String> tmp) {
        String tag = tmp.get(KeyConstants.MEDIA_TAG);
        String child, dir;
        if (tag != null && tag.equalsIgnoreCase("audio")) {
            child = tmp.get(KeyConstants.TITLE) + ".mp3";
            dir = Environment.DIRECTORY_MUSIC;
        }
        else if (tag != null && tag.equalsIgnoreCase("video")) {
            child = tmp.get(KeyConstants.TITLE) + ".mp4";
            dir = Environment.DIRECTORY_MOVIES;
        }
        else {
            child = tmp.get(KeyConstants.TITLE) + ".pdf";
            dir = Environment.DIRECTORY_DOCUMENTS;
        }
        baseFile = new File(context.getExternalFilesDir(dir), child);
        bookPath = baseFile.getPath();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(tmp.get(KeyConstants.MEDIA_URL)))
                .setTitle(context.getString(R.string.downloading))
                .setDescription(tmp.get(KeyConstants.TITLE))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(baseFile))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);
    }
}
