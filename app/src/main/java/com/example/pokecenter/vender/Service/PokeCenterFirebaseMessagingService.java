package com.example.pokecenter.vender.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.pokecenter.R;
import com.example.pokecenter.vender.VenderTab.Home.VenderNotificationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PokeCenterFirebaseMessagingService extends FirebaseMessagingService {
    private static SharedPreferences sharedPref;
    public static String getToken() {
        return sharedPref.getString("token", "");
    }

    public static final String CHANNEL_ID = "push_notification_id";

    public static void setToken(String value) {
        sharedPref.edit().putString("token", value).apply();
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e("TAG", token);
        setToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        sendNotification(message.getData().get("title"), message.getData().get("content"));
    }

    public void sendNotification(String strTitle, String strContent) {
        Intent intent = new Intent(this, VenderNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(strTitle)
                .setContentText(strContent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
