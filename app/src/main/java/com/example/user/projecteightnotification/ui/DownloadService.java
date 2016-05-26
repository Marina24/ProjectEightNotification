package com.example.user.projecteightnotification.ui;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.user.projecteightnotification.util.ManagerOfNotification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


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

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ManagerOfNotification.createNotification(this, intent);
        }

    }

    /**
     * Opening connection to internet and
     * get input stream
     **/
    public static void downloadData(String requestUrl) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            int statusCode = urlConnection.getResponseCode();

            // 200 represents HTTP OK
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                saveImageFromInputStream(inputStream);
            } else {
                Log.d("Download", "Failed to fetch data!!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saving file from input stream to
     * external storage
     */
    public static void saveImageFromInputStream(InputStream inputStream) {
        // Get path where save images
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File file = new File(path, "downloadImage.JPEG");
        // Get bitmap from input stream
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        OutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            //Save image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bufferedOutputStream);

            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
