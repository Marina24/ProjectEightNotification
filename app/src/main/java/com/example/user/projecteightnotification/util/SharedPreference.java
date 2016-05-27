package com.example.user.projecteightnotification.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SharedPreference {

    public static final String LAST_DOWNLOAD_TIME = "LAST TIME";

    /**
     * Save time of the last download in the
     * form of key-value
     */
    public static void setTimeOfLatDownload(Context context) {
        // Get time of the last download
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss").format(Calendar.getInstance().getTime());
        SharedPreferences preferencesLastDownload = context.getSharedPreferences(LAST_DOWNLOAD_TIME, Context.MODE_PRIVATE);
        // Set time of the last download
        SharedPreferences.Editor editor = preferencesLastDownload.edit();
        editor.putString("inter", timeStamp);
        editor.commit();
    }

}
