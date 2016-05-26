package com.example.user.projecteightnotification.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, DownloadService.class);
        serviceIntent.setAction(DownloadService.ACTION_CALLER);
        // Starting download service
        context.startService(serviceIntent);
    }
}
