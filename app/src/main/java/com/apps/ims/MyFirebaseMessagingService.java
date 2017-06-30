package com.apps.ims;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by androids on 2017/2/25.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d("FCM", "onMessageReceived:"+remoteMessage.getFrom());
//
        FirebaseMessaging.getInstance().subscribeToTopic("dogs");


        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getTag());

            sendNotification(remoteMessage);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainTab.class);

        if (remoteMessage.getData().size() > 0) {

            Log.w("IssueID", remoteMessage.getData().get("key").toString());

            if (!TextUtils.isEmpty(remoteMessage.getData().get("key").toString())) {
                intent = new Intent(this, IssueInfo.class);

                Bundle bundle = new Bundle();

                bundle.putString("IssueID", remoteMessage.getData().get("value"));

                intent.putExtras(bundle);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// this is a my insertion looking for a solution
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? com.apps.ims.R.mipmap.ims_app_icon : com.apps.ims.R.mipmap.ims_app_icon;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(remoteMessage.getFrom())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}