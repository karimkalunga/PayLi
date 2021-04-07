package com.media.clouds.app.dal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.media.clouds.app.R;

@Database(entities = {DownloadedMedia.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract DownloadedMediaDao libraryMediaDao();

    /**
     * Singleton initialization.
     * @param context application context.
     * @return database instance.
     */
    public static AppDatabase getDatabase(final Context context) {
        String databaseName = context.getString(R.string.app_name)
                .trim()
                .toLowerCase();
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, databaseName)
                            .build();
                }
            }
        }
        return instance;
    }
}
