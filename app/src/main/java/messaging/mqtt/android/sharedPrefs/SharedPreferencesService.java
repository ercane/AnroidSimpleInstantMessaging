package messaging.mqtt.android.sharedPrefs;

import android.content.SharedPreferences;


public class SharedPreferencesService {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public SharedPreferencesService(SharedPreferences preferences) {
        SharedPreferencesService.preferences = preferences;
        editor = preferences.edit();
    }

    public Long getEnrollmentDate() {
        return preferences.getLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, 0);
    }

    public void setEnrollmentDate(Long s) {
        editor.putLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, s);
        editor.commit();
    }

    public String getPassword() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_PASSWORD, "");
    }

    public void setPassword(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_PASSWORD, s);
        editor.commit();
    }
}
