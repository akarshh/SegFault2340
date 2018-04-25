package com.segfault.homelessshelter;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Singleton for logging
 */
public class Logger {

    private static Logger logger;
    private Context context;

    private Logger(Context context) {
        this.context = context;
    }

    // Public methods

    public static Logger getInstance(Context context) {
        if(logger == null) {
            return new Logger(context);
        } else {
            return logger;
        }
    }

    public void printMessage(String message) {
        print(getDate() + ": Message: " + message + "\n");
    }

    public void printWarning(String warning) {
        print(getDate() + ": WARNING: " + warning + "\n");
    }

    public String getLog() {
        try {
            FileInputStream logFIS = context.openFileInput("log");
            InputStreamReader logISR = new InputStreamReader(logFIS);
            BufferedReader logReader = new BufferedReader(logISR);
            StringBuilder logBuilder = new StringBuilder();
            String line = logReader.readLine();
            while(line != null) {
                logBuilder.append(line);
                logBuilder.append("\n");
                line = logReader.readLine();
            }
            logFIS.close();
            logISR.close();
            logReader.close();
            return logBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper methods

    private void print(String text) {
        try {
            FileOutputStream logFile = context.openFileOutput("log", Context.MODE_APPEND);
            logFile.write(text.getBytes());
            logFile.close();
        } catch (IOException e) {
            Log.e("ERROR:", e.getLocalizedMessage());
        }
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return format.format(date);
    }
}
