package com.example.user.projecteightnotification.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.projecteightnotification.Const;
import com.example.user.projecteightnotification.R;


public class ResultActivity extends AppCompatActivity {

    private Button mBtnUpDate;
    private TextView mTxtLastDataDownloaded;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set activity layout
        setContentView(R.layout.activity_result);
        // Initialize
        mBtnUpDate = (Button) findViewById(R.id.btn_up_date);
        mTxtLastDataDownloaded = (TextView) findViewById(R.id.txt_data_last_downloader);

        // Get data and time of last downloaded file
        mPreferences = getApplicationContext().getSharedPreferences(DownloadService.LAST_DOWNLOAD_TIME, Context.MODE_PRIVATE);
        final String inter = mPreferences.getString("inter", "default_no");
        // Update text
        mTxtLastDataDownloaded.setText("Last downloaded file " + inter);

        mBtnUpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(ResultActivity.this, DownloadService.class);
                // Send optional extras to download service
                updateIntent.putExtra(DownloadService.EXTRA_IMAGE_URI, Const.IMAGE_URI);
                updateIntent.setAction(DownloadService.ACTION_UPDATE);
                // Starting download service
                startService(updateIntent);
                // Finish activity
                finish();
            }
        });

    }
}
