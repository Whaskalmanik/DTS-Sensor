package com.whaskalmanik.dtssensor.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.BoringLayout;

import java.util.function.Consumer;

//todo dodelat dalsi nastaveni
public final class Preferences {
    private static Preferences instance;

    private SharedPreferences sharedPreferences;
    private Consumer<String> onSelectedChanged;

    private static void editValue(Consumer<SharedPreferences.Editor> editAction)
    {
        if (editAction == null) {
            return;
        }
        SharedPreferences.Editor editor = instance.sharedPreferences.edit();
        editAction.accept(editor);
        editor.apply();
    }

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

    public static String getSelectedValue()
    {
        return instance.sharedPreferences.getString("selected",null);
    }

    public static boolean isFirstStart()
    {
        return instance.sharedPreferences.getBoolean("first_start",true);
    }

    public static float getGraphOffset()
    {
        return Float.parseFloat(instance.sharedPreferences.getString("graph_offset","0.0f"));
    }

    public static void setFirstStart(boolean value)
    {
        editValue(x -> x.putBoolean("first_start",value));
    }

    public static void setSelectedValue(String value)
    {
        editValue(x -> x.putString("selected",value));
        if (instance.onSelectedChanged != null) {
            instance.onSelectedChanged.accept(value);
        }
    }

    public static void listenOnSelectedChanged(Consumer<String> action) {
        instance.onSelectedChanged = action;
    }
}