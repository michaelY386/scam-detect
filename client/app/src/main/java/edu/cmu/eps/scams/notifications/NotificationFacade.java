package edu.cmu.eps.scams.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import edu.cmu.eps.scams.R;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

/**
 * Created by jeremy on 3/4/2018.
 * This class wraps around the notification interface provided by Android.
 */

public class NotificationFacade {


    private static final String TAG = "NotificationFacade";

    private static final String CHANNEL_ID = "EPS-SCAMS-CHANNEL";

    private int notificationIdCounter;

    public NotificationFacade(Context context) {
        this.notificationIdCounter = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification buildService(Context context, PendingIntent pendingIntent, String title, String text) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.ic_mode_comment_black_24dp)
                        .setContentIntent(pendingIntent)
                        .build();
    }

    public void create(Context context, String title, String text) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mode_comment_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(this.nextNotificationId(), notificationBuilder.build());
    }

    public void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(notificationId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void createWithResponse(Context context, String title, String text, String respondTo) {
        int notificationId = this.nextNotificationId();
        Intent allowIntent = new Intent(context, MessageResponseReceiver.class);
        allowIntent.setAction("edu.cmu.eps.scams.ALLOW_MESSAGE");
        allowIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        allowIntent.putExtra("notificationId", notificationId);
        allowIntent.putExtra("respondTo", respondTo);
        PendingIntent allowPendingIntent = PendingIntent.getBroadcast(context, 0, allowIntent, 0);
        Intent blockIntent = new Intent(context, MessageResponseReceiver.class);
        blockIntent.setAction("edu.cmu.eps.scams.BLOCK_MESSAGE");
        blockIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);
        blockIntent.putExtra("notificationId", notificationId);
        blockIntent.putExtra("respondTo", respondTo);
        PendingIntent blockPendingIntent =
                PendingIntent.getBroadcast(context, 0, blockIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mode_comment_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_menu_share, context.getString(R.string.allow), allowPendingIntent)
                .addAction(R.drawable.ic_menu_share, context.getString(R.string.block), blockPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public int nextNotificationId() {
        int value = this.notificationIdCounter;
        this.notificationIdCounter = this.notificationIdCounter + 1;
        return value;
    }
}
