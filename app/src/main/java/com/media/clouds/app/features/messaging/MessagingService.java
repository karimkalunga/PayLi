package com.media.clouds.app.features.messaging;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.media.clouds.app.dal.Preferences;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Preferences.getInstance(this).saveFCMTokenId(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

    }
}
