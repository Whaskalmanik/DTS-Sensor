package com.whaskalmanik.dtssensor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.whaskalmanik.dtssensor.R;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private static boolean databaseKey = false;
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
    String stringValue = newValue.toString();
    if (preference instanceof ListPreference) {
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);
        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
    }
    else {
        if(stringValue.isEmpty()) {
            return false;
        }
        preference.setSummary(stringValue);
    }
    if(preference.getKey().equals("database_ip") || preference.getKey().equals("database_port")||preference.getKey().equals("database_name"))
    {
        databaseKey=true;
    }
    else
    {
        databaseKey=false;
    }
    return true;
    };

    @Override
    public void onBackPressed() {
        if(databaseKey)
        {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",1);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            bindPreferenceSummaryToValue(findPreference("warning_marker"));
            bindPreferenceSummaryToValue(findPreference("critical_marker"));
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
            bindPreferenceSummaryToValue(findPreference("database_name"));
            bindPreferenceSummaryToValue(findPreference("database_ip"));
            bindPreferenceSummaryToValue(findPreference("database_port"));
            bindPreferenceSummaryToValue(findPreference("graph_offset"));
            bindPreferenceSummaryToValue(findPreference("graph_heat_max"));
            bindPreferenceSummaryToValue(findPreference("graph_heat_min"));

        }
    }
}