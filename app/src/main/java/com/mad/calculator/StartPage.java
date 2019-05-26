package com.mad.calculator;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartPage extends AppCompatActivity
{

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler pHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        progressBar = findViewById(R.id.progressBar1);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                .getColor(R.color.textColor),PorterDuff.Mode.SRC_IN);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus++;
                    android.os.SystemClock.sleep(20);
                    pHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }

                if(progressStatus == 100)
                {
                    Intent intent = new Intent(StartPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }
}
