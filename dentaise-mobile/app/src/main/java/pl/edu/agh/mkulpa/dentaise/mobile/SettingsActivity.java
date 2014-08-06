package pl.edu.agh.mkulpa.dentaise.mobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Map;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.Repositories;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onStart() {
            super.onStart();
            SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
            preferences.registerOnSharedPreferenceChangeListener(this);
            initSummaries(preferences);
        }

        private void initSummaries(SharedPreferences preferences) {
            for (Map.Entry<String, ?> e : preferences.getAll().entrySet()) {
                onSharedPreferenceChanged(preferences, e.getKey());
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference changedPreference = findPreference(key);
            String value = ((EditTextPreference) changedPreference).getText();
            if ("pref_username".equals(key)) {
                Repositories.app.setUsername(value);
                changedPreference.setSummary(value);
            } else if ("pref_password".equals(key)) {
                Repositories.app.setPassword(value);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < value.length(); i++) {
                    builder.append('*');
                }
                changedPreference.setSummary(builder.toString());
            } else if ("pref_serverAddress".equals(key)) {
                Repositories.app.setServerAddress(value);
                changedPreference.setSummary(value);
            }
            Repositories.app.reconfigure();
        }

    }

}
