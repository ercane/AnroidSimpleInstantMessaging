package messaging.mqtt.android.sharedPrefs;

import android.content.SharedPreferences;


public class SharedPreferencesService {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public SharedPreferencesService(SharedPreferences preferences) {
        this.preferences = preferences;
        editor = preferences.edit();
    }

    public String getMdmId() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_MDM_ID, "");
    }

    public void setMdmId(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_MDM_ID, s);
        editor.commit();
    }

    public int getAppVersion() {
        return preferences.getInt(SharedPreferenceKeys.PROPERTY_APP_VERSION, 1);
    }

    public void setAppVersion(int s) {
        editor.putInt(SharedPreferenceKeys.PROPERTY_APP_VERSION, s);
        editor.commit();
    }

    public String getEnrollmentId() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_ENROLLMENT_ID, "");
    }

    public void setEnrollmentId(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_ENROLLMENT_ID, s);
        editor.commit();
    }

    public Long getEnrollmentDate() {
        return preferences.getLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, 0);
    }

    public void setEnrollmentDate(Long s) {
        editor.putLong(SharedPreferenceKeys.PROPERTY_PASS_DATE, s);
        editor.commit();
    }

    public Long getMsgKeyExpiredTime() {
        return preferences.getLong(SharedPreferenceKeys.PROPERTY_MSG_KEY_EXPIRED_TIME, 0);
    }

    public void setMsgKeyExpiredTime(Long s) {
        editor.putLong(SharedPreferenceKeys.PROPERTY_MSG_KEY_EXPIRED_TIME, s);
        editor.commit();
    }

    public int getOwnershipType() {
        return preferences.getInt(SharedPreferenceKeys.PROPERTY_OWNERSHIP_TYPE, -1);
    }

    public void setOwnershipType(int s) {
        editor.putInt(SharedPreferenceKeys.PROPERTY_OWNERSHIP_TYPE, s);
        editor.commit();
    }

    public boolean getIsAgreed() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_AGREED, false);
    }

    public void setIsAgreed(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_AGREED, b);
        editor.commit();
    }

    public String getDeviceToken() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_DEVICE_TOKEN, "");
    }

    public void setDeviceToken(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_DEVICE_TOKEN, b);
        editor.commit();
    }

    public String getAuthToken() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_AUTH_TOKEN, "");
    }

    public void setAuthToken(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_AUTH_TOKEN, b);
        editor.commit();
    }

    public String getSenderId() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_SENDER_ID, "");
    }

    public void setSenderId(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_SENDER_ID, s);
        editor.commit();
    }

    public String getServerIp() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_SERVER_IP, "");
    }

    public void setServerIp(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_SERVER_IP, s);
        editor.commit();
    }

    public String getRegId() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_REG_ID, "");
    }

    public void setRegId(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_REG_ID, s);
        editor.commit();
    }

    public String getUsername() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_USERNAME, "");
    }

    public void setUsername(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_USERNAME, s);
        editor.commit();
    }

    public String getPassword() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_PASSWORD, "");
    }

    public void setPassword(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_PASSWORD, s);
        editor.commit();
    }

    public String getSimSerialNumber() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_SIM_SERIAL_NUMBER, "");
    }

    public void setSimSerialNumber(String s) {
        editor.putString(SharedPreferenceKeys.PROPERTY_SIM_SERIAL_NUMBER, s);
        editor.commit();
    }

    public boolean getIsRegIdSend() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_REG_ID_SEND, false);
    }

    public void setIsRegIdSend(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_REG_ID_SEND, b);
        editor.commit();
    }

    public String getCaCert() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_CA_CERT, "");
    }

    public void setCaCert(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_CA_CERT, b);
        editor.commit();
    }

    public String getMsgKey() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_MSG_KEY, "");
    }

    public void setMsgKey(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_MSG_KEY, b);
        editor.commit();
    }

    public String getDbKey() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_DB_KEY, "");
    }

    public void setDbKey(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_DB_KEY, b);
        editor.commit();
    }

    public String getPublicKey() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_PUBLIC_KEY, "");
    }

    public void setPublicKey(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_PUBLIC_KEY, b);
        editor.commit();
    }

    public String getPrivateKey() {
        return preferences.getString(SharedPreferenceKeys.PROPERTY_PROVATE_KEY, "");
    }

    public void setPrivateKey(String b) {
        editor.putString(SharedPreferenceKeys.PROPERTY_PROVATE_KEY, b);
        editor.commit();
    }

    public boolean getIsSavedDefaultProfiles() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_SAVED_DEFAULT_PROFILES, false);
    }

    public void setIsSavedDefaultProfiles(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_SAVED_DEFAULT_PROFILES, b);
        editor.commit();
    }

    public boolean getIsSavedDefaultPolicies() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_SAVED_DEFAULT_POLICIES, false);
    }

    public void setIsSavedDefaultPolicies(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_SAVED_DEFAULT_POLICIES, b);
        editor.commit();
    }

    public boolean getIsKnoxSdkEmlActivated() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_KNOX_SDK_EML_ACTIVATED, false);
    }

    public void setIsKnoxSdkEmlActivated(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_KNOX_SDK_EML_ACTIVATED, b);
        editor.commit();
    }

    public boolean getIsKnoxSdkKmlActivated() {
        return preferences.getBoolean(SharedPreferenceKeys.PROPERTY_IS_KNOX_SDK_KML_ACTIVATED, false);
    }

    public void setIsKnoxSdkKmlActivated(Boolean b) {
        editor.putBoolean(SharedPreferenceKeys.PROPERTY_IS_KNOX_SDK_KML_ACTIVATED, b);
        editor.commit();
    }


}
