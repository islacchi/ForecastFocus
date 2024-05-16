package com.example.forecastfocus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.widget.ProgressBar;

public class LoadingScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        progressBar = findViewById(R.id.pb);

        // Start a new thread for progress simulation
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int totalProgress = 100; // Final target progress
                for (int j = 0; j <= 10; j++) {
                    int currentProgress = j * 10; // Calculate current progress
                    // Update progress on UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(currentProgress);
                        }
                    });
                    try {
                        Thread.sleep(400); // Delay in the separate thread
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Start MainActivity after loading completes (on UI thread)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoadingScreen.this, SignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }
}