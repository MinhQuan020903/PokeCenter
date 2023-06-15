package com.example.pokecenter.admin.AdminTab.Service;

//public class AdminNotificationService extends FirebaseMessagingService {
//    private static final String TAG = "AdminNotificationService";
//    private static final int CUSTOMER_REQUEST_RESPONSE_ID = 1106;
//
//    public AdminNotificationService() {
//        //Something
//    }
//    private Class<?> targetActivity;
//
//    public AdminNotificationService(Class<?> targetActivity) {
//        this.targetActivity = targetActivity;
//    }
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // handle a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//
//            sendNotification(remoteMessage.getNotification().getBody(), targetActivity);
//        }
//    }
//
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        sendRegistrationToServer(token);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // TODO: Implement this method to send token to your app server.
//    }
//
//    private void sendNotification(String messageBody, Class<?> targetActivity) {
//        Intent intent = new Intent(this, targetActivity);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.project_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
//                        .setContentTitle(getString(R.string.project_id))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                        .addAction(new NotificationCompat.Action(
//                                android.R.drawable.sym_call_missed,
//                                "Cancel",
//                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
//                        .addAction(new NotificationCompat.Action(
//                                android.R.drawable.sym_call_outgoing,
//                                "OK",
//                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//}


