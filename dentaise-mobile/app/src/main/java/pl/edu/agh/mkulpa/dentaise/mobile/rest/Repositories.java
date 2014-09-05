package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * Created by byyyk on 05.08.14.
 */
public class Repositories {
    public static AppRepository app;
    public static PatientRepository patient;
    public static VisitRepository visit;

    public static void setup(Context context) {
        //TODO configure in settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        app = new AppRepository(
                preferences.getString("pref_serverAddress", null),
                preferences.getString("pref_username", null),
                preferences.getString("pref_password", null)
        );
        patient = new PatientRepository(app);
        visit = new VisitRepository(app);
    }
}
