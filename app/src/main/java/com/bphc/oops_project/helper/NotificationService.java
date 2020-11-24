package com.bphc.oops_project.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.bphc.oops_project.MainActivity2;
import com.bphc.oops_project.R;
import com.bphc.oops_project.prefs.SharedPrefs;

public class NotificationService extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public final static String DEFAULT_NOTIFICATION_CHANNEL_ID = "default";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        sendNotification(title, description, id, context);
    }

    private void sendNotification(String title, String description, int id, Context context) {
        Intent intent = new Intent(context, MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(id, builder.build());
    }
}
