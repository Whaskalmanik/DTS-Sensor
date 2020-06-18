package com.whaskalmanik.dtssensor.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.function.Consumer;

public final class Preferences {
    private static Preferences instance;

    private SharedPreferences sharedPreferences;
    private Consumer<String> onSelectedChanged;

    private static void editValue(Consumer<SharedPreferences.Editor> editAction) {
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

    public static float getWarningTemp() {
        return Float.parseFloat(instance.sharedPreferences.getString("warning_marker","0.0f"));
    }

    public static float getCriticalTemp() {
        return Float.parseFloat(instance.sharedPreferences.getString("critical_marker","0.0f"));
    }

    public static boolean areMarkersEnabled() {
        return instance.sharedPreferences.getBoolean("marker_switch",true);
    }

    public static boolean isSynchronizationEnabled() {
        return instance.sharedPreferences.getBoolean("synchronizations_switch",true);
    }

    public static boolean isDataOverrided() {
        return instance.sharedPreferences.getBoolean("heat_graph_switch",false);
    }
    public static boolean isMaxDataManualyEdited() {
        return instance.sharedPreferences.getBoolean("max_data_edited",false);
    }
    public static void setMaxDataManuallyEdited(final boolean value)
    {
        editValue(x -> x.putBoolean("max_data_edited", value));
    }

    public static int getFrequency() {
        return Integer.parseInt(instance.sharedPreferences.getString("sync_frequency","10"));
    }

    public static int getHeatMax() {
        return Integer.parseInt(instance.sharedPreferences.getString("graph_heat_max","30"));
    }

    public static int getHeatMin() {
        return Integer.parseInt(instance.sharedPreferences.getString("graph_heat_min","20"));
    }

    public static String getIP() {
        return instance.sharedPreferences.getString("database_ip","192.168.4.1");
    }

    public static int getPort() {
        return Integer.parseInt(instance.sharedPreferences.getString("database_port","27017"));
    }

    public static String getDatabaseName() {
        return instance.sharedPreferences.getString("database_name","DTS");
    }

    public static String getSelectedValue() {
        return instance.sharedPreferences.getString("selected",null);
    }

    public static boolean isFirstStart() {
        return instance.sharedPreferences.getBoolean("first_start",true);
    }

    public static float getGraphOffsetMin() {
        return Float.parseFloat(instance.sharedPreferences.getString("graph_offset_min","0.0f"));
    }

    public static float getGraphOffsetMax() {
        return Float.parseFloat(instance.sharedPreferences.getString("graph_offset_max",String.valueOf(Float.MAX_VALUE)));
    }

    public static void setGraphOffsetMax(final float value) {
        editValue(x -> x.putString("graph_offset_max", String.valueOf(value)));
    }

    public static void setFirstStart(boolean value) {
        editValue(x -> x.putBoolean("first_start",value));
    }

    public static void setSelectedValue(String value) {
        editValue(x -> x.putString("selected",value));
        if (instance.onSelectedChanged != null) {
            instance.onSelectedChanged.accept(value);
        }
    }

    public static void listenOnSelectedChanged(Consumer<String> action) {
        instance.onSelectedChanged = action;
    }
}