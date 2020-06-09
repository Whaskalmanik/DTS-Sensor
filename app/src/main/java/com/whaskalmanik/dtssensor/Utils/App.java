package com.whaskalmanik.dtssensor.Utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {
    public static final String CHANNEL_1_ID = "channel_1";
    public static final String CHANNEL_2_ID = "channel_2";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel_l;
            NotificationChannel channel_2;

            channel_l = new NotificationChannel(CHANNEL_1_ID,"CriticalMarker",NotificationManager.IMPORTANCE_HIGH);
            channel_l.setDescription("Channel for critical markers");

            channel_2 = new NotificationChannel(CHANNEL_2_ID,"WarningMarker",NotificationManager.IMPORTANCE_DEFAULT);
            channel_2.setDescription("Channel for warning markers");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel_l);
                manager.createNotificationChannel(channel_2);
            }
        }
    }
}
