package com.whaskalmanik.dtssensor.Preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MarkersPref {
    private SharedPreferences sharedPreferences;
    Context context;
    public MarkersPref(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public float getWarningTemp()
    {
        return Float.parseFloat(sharedPreferences.getString("warning_marker",""));
    }

    public float getCriticalTemp()
    {
        return Float.parseFloat(sharedPreferences.getString("critical_marker",""));
    }

    public boolean isEnabled()
    {
        return sharedPreferences.getBoolean("marker_switch",true);
    }


}
