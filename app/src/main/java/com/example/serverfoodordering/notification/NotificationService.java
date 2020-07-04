package com.example.serverfoodordering.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.serverfoodordering.OrderManagement;
import com.example.serverfoodordering.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String message = notification.getBody();

        String CHANNEL_1_ID = "channel2";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_HIGH
            );

            Intent intent = new Intent(this, OrderManagement.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            Notification build = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(new Random().nextInt(), build);
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(title)
                    .setContentText(message);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(new Random().nextInt(), builder.build());
        }
    }
}
