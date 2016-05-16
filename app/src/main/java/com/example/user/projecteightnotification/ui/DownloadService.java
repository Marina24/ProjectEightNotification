package com.example.user.projecteightnotification.ui;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.user.projecteightnotification.Const;
import com.example.user.projecteightnotification.R;
import com.example.user.projecteightnotification.util.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * An DownloadService subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class DownloadService extends IntentService {

    // Action that describe tasks
    public static final String ACTION_START_DOWNLOAD = "com.example.user.projecteightnotification.ui.action.START_DOWNLOAD";
    public static final String ACTION_CALLER = "com.example.user.projecteightnotification.ui.action.ACTION_CALLER";
    public static final String ACTION_UPDATE = "com.example.user.projecteightnotification.ui.action.ACTION_UPDATE ";

    // Extras parameters
    public static final String EXTRA_IMAGE_URI = "com.example.user.projecteightnotification.ui.extra.ARRAY_URI";

    // Notification identifier
    public static final int NOTIFY_ID = 101;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Uri mRingURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    public static final String LAST_DOWNLOAD_TIME = "LAST TIME";
    private SharedPreferences mSpLastDownload;


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (intent != null) {
            if (ACTION_START_DOWNLOAD.equals(action)) {
                mainDownloadingNotification(intent);
            }
            if (ACTION_CALLER.equals(action)) {
                rebootNotification();
            }
            if (ACTION_UPDATE.equals(action)) {
                upDateNotification(intent);
            }
        }

    }

    /**
     * Notification after reboot device
     */
    private void rebootNotification() {
        // Create notification
        createUpdateNotification("Update file is available!", "", mRingURI);
        // Display notification
        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(NOTIFY_ID, mBuilder.build());
    }


    /**
     * Update notification with process of downloading by button
     *
     * @param intent - uri of file for downloading
     */
    private void upDateNotification(Intent intent) {
        // Create notification
        createUpdateNotification("Download", "0%", null);

        processDownloading(intent);
    }

    /**
     * Creating notification of first downloading file
     *
     * @param intent - uri of file for downloading
     */
    private void mainDownloadingNotification(Intent intent) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= 11) {
            // Create notification
            mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_file_download).
                    setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_downloader)).
                    setTicker("download...").
                    setContentTitle("Download").
                    setContentText("0%");
        } else {
            PendingIntent pendingintent = PendingIntent.getActivity(this, 1, null, 0);
            // Create notification
            mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_file_download).
                    setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_downloader)).
                    setTicker("download...").
                    setContentTitle("Download").
                    setContentText("0%").
                    setContentIntent(pendingintent);
        }
        processDownloading(intent);
    }

    /**
     * Creating of update notification with  button
     *
     * @param contentTitle - title of notification
     * @param contextText  - text in notification
     * @param ring         - up on completion of loading
     */
    private void createUpdateNotification(String contentTitle, String contextText, Uri ring) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Clicking on
        // notification open result activity
        Intent intentSecond = new Intent(this, ResultActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentSecond, PendingIntent.FLAG_CANCEL_CURRENT);
        // Clicking on button in notification reloading file
        Intent intentUpDate = new Intent(this, DownloadService.class);
        intentUpDate.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
        intentUpDate.setAction(DownloadService.ACTION_UPDATE);
        PendingIntent pendingUpDate = PendingIntent.getService(this, 0, intentUpDate, PendingIntent.FLAG_UPDATE_CURRENT);
        // Create notification
        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_new_releases).
                setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_update)).
                setContentTitle(contentTitle).
                setContentIntent(pendingIntent).
                setSound(ring).
                addAction(R.drawable.ic_up_date_file, "Update", pendingUpDate).
                setContentText(contextText);
    }

    /**
     * Process of downloading file displayed by progress bar
     *
     * @param intent - uri of file for downloading
     */
    private void processDownloading(Intent intent) {
        // Get download link
        final String uri = intent.getStringExtra(EXTRA_IMAGE_URI);
        // Displays the progress bar for the first time
        mBuilder.setProgress(100, 0, false);
        // Downloading of file
        for (int j = 0; j <= 100; j++) {
            mBuilder.setSmallIcon(R.drawable.ic_file_download);
            mBuilder.setProgress(100, Math.min(j, 100), false);
            mBuilder.setContentText(Math.min(j, 100) + "% ");
            mNotifyManager.notify(NOTIFY_ID, mBuilder.build());
            UiUtils.downloadData(uri);
            try {
                // Sleep for a while
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d("Failure", "sleeping failure");
            }
        }
        Log.d("Download", "Save image!");
        // When the loop is finished, updates the notification
        mBuilder.setContentText("Download complete")
                .setSmallIcon(R.drawable.ic_done)
                // Removes the progress bar
                .setProgress(0, 0, false)
                .setSound(mRingURI);

        mBuilder.setAutoCancel(true);
        mNotifyManager.notify(NOTIFY_ID, mBuilder.build());

        setTimeOfLatDownload();
    }

    /**
     * Save time of the last download in the
     * form of key-value
     */
    private void setTimeOfLatDownload() {
        // Get time of the last download
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss").format(Calendar.getInstance().getTime());
        mSpLastDownload = getApplicationContext().getSharedPreferences(LAST_DOWNLOAD_TIME, Context.MODE_PRIVATE);
        // Set time of the last download
        SharedPreferences.Editor editor = mSpLastDownload.edit();
        editor.putString("inter", timeStamp);
        editor.commit();
    }
}
