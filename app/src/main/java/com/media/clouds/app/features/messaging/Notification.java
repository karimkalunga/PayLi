package com.media.clouds.app.features.messaging;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.media.clouds.app.R;
import com.media.clouds.app.utils.KeyConstants;

class Notification {
    private static final String CHANNEL_ID = "notify_me";
    private Context context;

    Notification (Context context) { this.context = context; }

    public void show(Intent intent, Bundle bundle) {
        String title = "", contentText = "";
        if (bundle != null) {
            title = bundle.getString(KeyConstants.NOTIFICATION_TITLE);
            contentText = bundle.getString(KeyConstants.NOTIFICATION_CONTENT);
        }
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_logo_icon)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setSound(soundUri)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
