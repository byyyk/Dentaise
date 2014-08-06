package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import pl.edu.agh.mkulpa.dentaise.mobile.R;

/**
 * Created by byyyk on 06.08.14.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}