package com.whaskalmanik.dtssensor.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Resources;
import android.os.Build;

import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.R;


public class App extends Application {
    public static final String CHANNEL_1_ID = "channel_1";
    public static final String CHANNEL_2_ID = "channel_2";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Preferences.initialize(getApplicationContext());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel_l;
            NotificationChannel channel_2;
            Resources res= getResources();

            channel_l = new NotificationChannel(CHANNEL_1_ID,res.getString(R.string.criticalMarker),NotificationManager.IMPORTANCE_HIGH);
            channel_l.setDescription(res.getString(R.string.channel_desc_critical));

            channel_2 = new NotificationChannel(CHANNEL_2_ID,res.getString(R.string.warningMarker),NotificationManager.IMPORTANCE_DEFAULT);
            channel_2.setDescription(res.getString(R.string.channel_desc_warning));

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel_l);
                manager.createNotificationChannel(channel_2);
            }
        }
    }
}
