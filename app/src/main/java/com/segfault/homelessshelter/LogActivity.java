package com.segfault.homelessshelter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // Set view variables
        TextView logTextView = findViewById(R.id.logTextView);

        // Get Logger instance
        Context context = getApplicationContext();
        Logger logger = Logger.getInstance(context);

        // Populate TextView with log's text
        String log = logger.getLog();
        logTextView.setText(log);
    }
}
