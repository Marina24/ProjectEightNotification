package com.example.user.projecteightnotification.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.user.projecteightnotification.Const;
import com.example.user.projecteightnotification.R;

public class MainActivity extends AppCompatActivity {

    private Button mBtnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set activity layout
        setContentView(R.layout.activity_main);
        //Initialize button
        mBtnDownload = (Button) findViewById(R.id.btn_download);

        downloadFile();
    }

    /**
     * Starting download file from internet
     * by clicking on button
     */
    public void downloadFile() {
        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                // Send optional extras to download service
                intent.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
                intent.setAction(DownloadService.ACTION_START_DOWNLOAD);
                // Starting download service
                startService(intent);
                // Finish activity
                finish();
            }
        });
    }
}
