package com.example.user.projecteightnotification.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.projecteightnotification.Const;
import com.example.user.projecteightnotification.R;
import com.example.user.projecteightnotification.util.SharedPreference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonDownload;
    private Button mButtonUpDate;
    private TextView mTextLastDataDownloaded;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set activity layout
        setContentView(R.layout.activity_main);
        //Initialize
        mButtonDownload = (Button) findViewById(R.id.btn_download);
        mButtonUpDate = (Button) findViewById(R.id.btn_up_date);
        mTextLastDataDownloaded = (TextView) findViewById(R.id.txt_data_last_downloader);

        mButtonDownload.setOnClickListener(this);
        mButtonUpDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Starting download file from internet by clicking on button
            case R.id.btn_download:
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                // Send optional extras to download service
                intent.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
                intent.setAction(DownloadService.ACTION_START_DOWNLOAD);
                // Starting download service
                startService(intent);
                // Finish activity
                finish();
                break;
            case R.id.btn_up_date:
                Intent updateIntent = new Intent(MainActivity.this, DownloadService.class);
                // Send optional extras to download service
                updateIntent.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
                updateIntent.setAction(DownloadService.ACTION_UPDATE);
                // Starting download service
                startService(updateIntent);
                // Finish activity
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get data and time of last downloaded file
        mPreferences = getApplicationContext().getSharedPreferences(SharedPreference.LAST_DOWNLOAD_TIME, Context.MODE_PRIVATE);
        final String inter = mPreferences.getString("inter", "default_no");
        if (inter.equals("default_no")) {
            mButtonUpDate.setVisibility(View.INVISIBLE);
            mTextLastDataDownloaded.setVisibility(View.INVISIBLE);
        } else {
            mButtonUpDate.setVisibility(View.VISIBLE);
            mTextLastDataDownloaded.setVisibility(View.VISIBLE);
            // Update text
            mTextLastDataDownloaded.setText("Last downloaded file " + inter);
        }
    }
}
