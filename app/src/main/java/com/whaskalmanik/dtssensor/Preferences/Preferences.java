package com.whaskalmanik.dtssensor.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.BoringLayout;

//todo dodelat dalsi nastaveni
public final class Preferences {
    static Preferences instance;

    private SharedPreferences sharedPreferences;

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new Preferences();
        }
        instance.sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static float getWarningTemp()
    {
        return Float.parseFloat(instance.sharedPreferences.getString("warning_marker","0.0f"));
    }

    public static float getCriticalTemp()
    {
        return Float.parseFloat(instance.sharedPreferences.getString("critical_marker","0.0f"));
    }

    public static boolean areMarkersEnabled()
    {
        return instance.sharedPreferences.getBoolean("marker_switch",true);
    }

    public static boolean isSynchronizationEnabled()
    {
        return instance.sharedPreferences.getBoolean("synchronizations_switch",true);
    }

    public static int getFrequency()
    {
        return Integer.parseInt(instance.sharedPreferences.getString("sync_frequency","10"));
    }

    public static String getIP()
    {
        return instance.sharedPreferences.getString("database_ip","192.168.4.1");
    }

    public static int getPort()
    {
        return Integer.parseInt(instance.sharedPreferences.getString("database_port","27017"));
    }

    public static String getDatabaseName()
    {
        return instance.sharedPreferences.getString("database_name","DTS");
    }
}