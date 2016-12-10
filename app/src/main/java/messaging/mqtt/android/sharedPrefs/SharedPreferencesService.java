package messaging.mqtt.android.sharedPrefs;

import android.content.SharedPreferences;


public class SharedPreferencesService{

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public SharedPreferencesService(SharedPreferences preferences){
        SharedPreferencesService.preferences = preferences;
        editor = preferences.edit();
    }

    public Long getEnrollmentDate(){
        return preferences.getLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, 0);
    }

    public void setEnrollmentDate(Long s){
        editor.putLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, s);
        editor.commit();
    }

    public String getPassword(){
        return preferences.getString(SharedPreferenceKeys.PROPERTY_PASSWORD, "");
    }

    public void setPassword(String s){
        editor.putString(SharedPreferenceKeys.PROPERTY_PASSWORD, s);
        editor.commit();
    }

    public String getBrokerProtocol(){
        return preferences.getString(SharedPreferenceKeys.PROPERTY_BROKER_PROTOCOL, "");
    }

    public void setBrokerProtocol(String s){
        editor.putString(SharedPreferenceKeys.PROPERTY_BROKER_PROTOCOL, s);
        boolean commit = editor.commit();
    }

    public String getBrokerIp(){
        return preferences.getString(SharedPreferenceKeys.PROPERTY_BROKER_IP, "");
    }

    public void setBrokerIp(String s){
        editor.putString(SharedPreferenceKeys.PROPERTY_BROKER_IP, s);
        boolean commit = editor.commit();
    }

    public String getBrokerPort(){
        return preferences.getString(SharedPreferenceKeys.PROPERTY_BROKER_PORT, "");
    }

    public void setBrokerPort(String s){
        editor.putString(SharedPreferenceKeys.PROPERTY_BROKER_PORT, s);
        boolean commit = editor.commit();
    }
}
