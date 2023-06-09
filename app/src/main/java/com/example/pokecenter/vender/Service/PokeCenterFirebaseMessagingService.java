package com.example.pokecenter.vender.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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

        SpannableString spannableTitle = new SpannableString(strTitle);
        spannableTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
//        remoteViews.setTextViewText(R.id.notification_title, spannableTitle);
//        remoteViews.setTextViewText(R.id.notification_content, strContent);
//        remoteViews.setImageViewResource(R.id.notification_image, R.drawable.lam_spheal);
//        remoteViews.setViewVisibility(R.id.notification_image, View.VISIBLE);

        @SuppressLint("ResourceAsColor") NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(spannableTitle)
                .setContentText(strContent)
                .setSmallIcon(R.drawable.tin_pokeball_small_icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(strContent))
                .setContentIntent(pendingIntent);


        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
