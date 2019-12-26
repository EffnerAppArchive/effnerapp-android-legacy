package de.effnerapp.effner.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private static final String CHANNEL_ID = "Notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: "+ remoteMessage.getFrom());
        Log.d(TAG, "Message Body: "+ Objects.requireNonNull(remoteMessage.getNotification()).getBody() + ":" + remoteMessage.getMessageId());
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setPriority(1)
                .setAutoCancel(true);
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(2, builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Notification";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, "Token: " + s);
    }

}
