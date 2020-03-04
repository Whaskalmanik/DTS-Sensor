package com.whaskalmanik.dtssensor.Activities;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.whaskalmanik.dtssensor.Fragments.TemperatureFragment;
import com.whaskalmanik.dtssensor.Preference.MarkersPref;
import com.whaskalmanik.dtssensor.Preference.NotificationsPref;
import com.whaskalmanik.dtssensor.Preference.SynchronizationPref;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.NotificationHelper;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
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
    else if (preference instanceof RingtonePreference)
    {
        // For ringtone preferences, look up the correct display value
        // using RingtoneManager.
        if (TextUtils.isEmpty(stringValue))
        {
            // Empty values correspond to 'silent' (no ringtone).
            preference.setSummary(R.string.pref_ringtone_silent);
        }
        else
        {
            Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
            if (ringtone == null)
            {
                // Clear the summary if there was a lookup error.
                preference.setSummary(null);
            }
            else
            {
                // Set the summary to reflect the new ringtone display
                // name.
                String name = ringtone.getTitle(preference.getContext());
                preference.setSummary(name);
            }
        }
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
                        .getString(preference.getKey(), ""));
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
            bindPreferenceSummaryToValue(findPreference("notifications_ringtone"));
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
            bindPreferenceSummaryToValue(findPreference("sync_type"));
        }
    }
}
