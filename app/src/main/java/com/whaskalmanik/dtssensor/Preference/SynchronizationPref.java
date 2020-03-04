package com.whaskalmanik.dtssensor.Preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SynchronizationPref {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SynchronizationPref(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }


    public String getSyncType()
    {
        return sharedPreferences.getString("sync_type","");
    }

    public int getFrequency()
    {
        return sharedPreferences.getInt("sync_frequency",10);
    }

    public boolean isEnabled()
    {
        return sharedPreferences.getBoolean("synchronizations_switch",true);
    }
}
