package com.example.user.projecteightnotification.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.user.projecteightnotification.Const;
import com.example.user.projecteightnotification.R;
import com.example.user.projecteightnotification.ui.DownloadService;
import com.example.user.projecteightnotification.ui.MainActivity;


/**
 * Class for create notifications
 */
public class ManagerOfNotification {
    // Notification identifier
    public static final int NOTIFY_ID = 1;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    private static Uri mRingURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    public static void createNotification(Context context, Intent intent) {
        // Get download link
        final String uri = intent.getStringExtra(DownloadService.EXTRA_IMAGE_URI);
        // Create notification
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        int textColor = ContextCompat.getColor(context, R.color.textColor);
        contentView.setTextColor(R.id.txt_notif_title, textColor);
        contentView.setTextColor(R.id.txt_update_notif, textColor);
        // Get action intent
        final String action = intent.getAction();
        switch (action) {
            case DownloadService.ACTION_START_DOWNLOAD:
                fillNotification(context, R.drawable.ic_file_download, new Intent(context, MainActivity.class),
                        new Intent(context, DownloadService.class), R.drawable.ic_downloader, "Download",
                        View.GONE, View.VISIBLE, contentView);
                progressDownload(context, contentView, mBuilder.build(), "Downloading...",
                        uri, "Download complete", View.GONE, View.GONE, View.GONE);
                break;
            case DownloadService.ACTION_CALLER:
                fillNotification(context, R.drawable.ic_new_releases, new Intent(context, MainActivity.class),
                        new Intent(context, DownloadService.class), R.drawable.ic_update, "Update file is available!",
                        View.VISIBLE, View.GONE, contentView);
                break;
            case DownloadService.ACTION_UPDATE:
                fillNotification(context, R.drawable.ic_new_releases, new Intent(context, MainActivity.class),
                        new Intent(context, DownloadService.class), R.drawable.ic_update, "Update file is available!",
                        View.GONE, View.VISIBLE, contentView);
                progressDownload(context, contentView, mBuilder.build(), "Updating...",
                        uri, "Updating complete", View.VISIBLE, View.GONE, View.GONE);
                break;
        }
    }

    public static void fillNotification(Context context, int smallIcon, Intent intentActivity, Intent intentService,
                                        int imageView, String content, int visibilityUpdateButton,
                                        int visibilityProgressBar, RemoteViews contentView) {
        mBuilder.setSmallIcon(smallIcon);
        // Clicking on notification open result activity
        PendingIntent pendingintent = PendingIntent.getActivity(context, 1, intentActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        // Clicking on button in notification reloading file
        intentService.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
        intentService.setAction(DownloadService.ACTION_UPDATE);
        PendingIntent pendingUpDate = PendingIntent.getService(context, 0, intentService, PendingIntent.FLAG_CANCEL_CURRENT);

        // Using RemoteViews to bind custom layouts into Notification
        contentView.setImageViewResource(R.id.img_download_notification, imageView);
        contentView.setTextViewText(R.id.txt_notif_title, content);
        contentView.setViewVisibility(R.id.img_update, visibilityUpdateButton);
        contentView.setViewVisibility(R.id.progress_bar, visibilityProgressBar);
        contentView.setOnClickPendingIntent(R.id.img_update, pendingUpDate);
        contentView.setOnClickPendingIntent(R.id.layout_custom_notification, pendingintent);

        mBuilder.setContent(contentView);
        Notification notification = mBuilder.build();
        notification.contentIntent = pendingintent;
        notification.contentView = contentView;
        mNotifyManager.notify(NOTIFY_ID, notification);
    }


    public static void progressDownload(Context context, RemoteViews contentView, Notification notification,
                                        String title, String uri, String result, int visibilityImageUpDate,
                                        int visibilityUpdateButton, int visibilityProgressBar) {
        notification.contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        // Downloading of file
        for (int j = 0; j <= 100; j++) {
            contentView.setTextViewText(R.id.txt_notif_title, title);
            contentView.setProgressBar(R.id.progress_bar, 100, Math.min(j, 100), false);
            contentView.setTextViewText(R.id.txt_update_notif, Math.min(j, 100) + "% ");
            mBuilder.setContent(contentView);
            notification.contentView = contentView;
            mNotifyManager.notify(NOTIFY_ID, notification);
            DownloadService.downloadData(uri);
            try {
                // Sleep for a while
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d("Failure", "sleeping failure");
            }
        }
        // When the loop is finished, updates the notification
        contentView.setTextViewText(R.id.txt_notif_title, result);
        // Removes the progress bar
        contentView.setViewVisibility(R.id.img_update, visibilityImageUpDate);
        contentView.setViewVisibility(R.id.progress_bar, visibilityUpdateButton);
        contentView.setViewVisibility(R.id.txt_update_notif, visibilityProgressBar);
        mBuilder.setContent(contentView).setSmallIcon(R.drawable.ic_done).setSound(mRingURI).setAutoCancel(true);

        notification = mBuilder.build();
        notification.contentView = contentView;
        notification.contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        mNotifyManager.notify(NOTIFY_ID, notification);

        SharedPreference.setTimeOfLatDownload(context);
    }
}
