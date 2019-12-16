package com.example.lazymessanger;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public class CustomApplication extends Application {

    private static final String TAG = "CustomApplication";

    public static final float LIGHT_VALUE = 5;
    private static final String CHANNEL_ID = "notification";

    public static boolean THEME_NOT_SELECTED = true;
    public static int THEME = AppCompatDelegate.MODE_NIGHT_NO;
    public static String THEME_SHARED_PREFERENCE = "theme-mode";
    public static String THEME_SPECIFIED = "theme-specified";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
