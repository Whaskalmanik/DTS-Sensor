package com.whaskalmanik.dtssensor.Preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

public class NotificationsPref {
    private SharedPreferences sharedPreferences;
    private Context context;

    public NotificationsPref(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }

    public boolean getVibrations()
    {
        return sharedPreferences.getBoolean("notifications_vibrations_switch",true);
    }

    public Ringtone getRingtone()
    {
        String uri = sharedPreferences.getString( "notifications_ringtone","content://settings/system/notification_sound");
        Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse(uri));
        return ringtone;
    }

    public boolean isEnabled()
    {
        return sharedPreferences.getBoolean("notifications_switch",true);
    }

}
