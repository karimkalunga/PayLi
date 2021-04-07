package com.media.clouds.app.dal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DownloadedMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DownloadedMedia media);

    @Query("SELECT * FROM downloads ORDER BY id DESC")
    LiveData<List<DownloadedMedia>> getAllDownloads();

    @Query("SELECT COUNT(*) as total FROM downloads WHERE id=:id")
    int getAvailableDownload(int id);

    @Query("SELECT * FROM downloads")
    List<DownloadedMedia> getExpiredContent();

    @Query("DELETE FROM downloads WHERE id=:id")
    void purge(int id);
}
