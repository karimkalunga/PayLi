package com.media.clouds.app.features.media.library;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.media.clouds.app.dal.AppDatabase;
import com.media.clouds.app.dal.DownloadedMedia;
import com.media.clouds.app.dal.DownloadedMediaDao;

import java.util.List;

public class DownloadsViewModel extends AndroidViewModel {

    private DownloadedMediaDao downloadedMediaDao;
    private LiveData<List<DownloadedMedia>> downloadsList;

    public DownloadsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        downloadedMediaDao = db.libraryMediaDao();
        downloadsList = downloadedMediaDao.getAllDownloads();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    LiveData<List<DownloadedMedia>> getAllDownloads() {
        return downloadsList;
    }

    List<DownloadedMedia> getExpiredContent() {
        try {
            return new ExpiredAsyncTask(downloadedMediaDao).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getAvailableDownload(int id) {
        try {
            return new TotalDownloadAsyncTask(downloadedMediaDao).execute(id).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insert(DownloadedMedia media) {
        new InsertAsyncTask(downloadedMediaDao).execute(media);
    }

    private static class InsertAsyncTask extends AsyncTask<DownloadedMedia, Void, Void> {
        private DownloadedMediaDao downloadedMediaDao;

        InsertAsyncTask(DownloadedMediaDao downloadedMediaDao) {
            this.downloadedMediaDao = downloadedMediaDao;
        }

        @Override
        protected Void doInBackground(DownloadedMedia... media) {
            downloadedMediaDao.insert(media[0]);
            return null;
        }
    }

    private static class TotalDownloadAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private DownloadedMediaDao dDao;

        TotalDownloadAsyncTask(DownloadedMediaDao dDao) {
            this.dDao = dDao;
        }

        @Override
        protected Integer doInBackground(Integer... ids) {
            return dDao.getAvailableDownload(ids[0]);
        }
    }

    private static class purgeAsyncTask extends AsyncTask<Integer, Void, Void> {
        private DownloadedMediaDao dao;

        purgeAsyncTask(DownloadedMediaDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Integer... ids) {
            dao.purge(ids[0]);
            return null;
        }
    }

    private static class ExpiredAsyncTask extends AsyncTask<Void, Void, List<DownloadedMedia>> {
        private DownloadedMediaDao dao;

        ExpiredAsyncTask(DownloadedMediaDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<DownloadedMedia> doInBackground(Void... voids) {
            return dao.getExpiredContent();
        }
    }
}
