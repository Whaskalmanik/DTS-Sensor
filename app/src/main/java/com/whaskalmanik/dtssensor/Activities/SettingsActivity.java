package com.whaskalmanik.dtssensor.Activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.whaskalmanik.dtssensor.R;


public class SettingsActivity extends AppCompatPreferenceActivity
{
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
    String stringValue = newValue.toString();

    if (preference instanceof ListPreference)
    {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
    }
    else
    {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.setSummary(stringValue);
    }
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "0"));
    }

    public static class MainPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
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
