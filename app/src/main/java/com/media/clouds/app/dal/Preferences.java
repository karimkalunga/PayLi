package com.media.clouds.app.dal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.media.clouds.app.R;
import com.media.clouds.app.utils.KeyConstants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Preferences.class
 * This class holds shared preferences implementations.
 */
public class Preferences {

    private static Preferences instance = null;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    /**
     * Constructor.
     */
    @SuppressLint("CommitPrefEdits")
    private Preferences(Context context) {
        String name = context.getString(R.string.app_name);
        editor = context.getSharedPreferences(name, MODE_PRIVATE).edit();
        prefs = context.getSharedPreferences(name, MODE_PRIVATE);
    }

    /**
     * Singleton.
     * @return shared preference instance.
     */
    public static synchronized Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    /**
     * Sets Firebase Cloud Messaging token id.
     * @param token FCM token.
     */
    public void saveFCMTokenId(String token) {
        editor.putString(KeyConstants.FCMTOKEN, token);
        editor.apply();
    }

    /**
     * Gets stored Firebase Cloud Messaging token id.
     * @return FCM token string.
     */
    public String getFCMTokenId() {
        return prefs.getString(KeyConstants.FCMTOKEN, "");
    }

    /**
     * Sets if user previously logged into the app.
     * @param status true/false.
     */
    public void setLoginStatus(boolean status) {
        editor.putBoolean(KeyConstants.STATUS, status);
        editor.apply();
    }

    /**
     * Checks if user previously logged into the app.
     * @return true/false.
     */
    public boolean getLoginStatus() {
        return prefs.getBoolean(KeyConstants.STATUS, false);
    }

    /**
     * Sets user identification.
     * @param id unique ID from the server.
     */
    public void setUserId(String id) {
        editor.putString(KeyConstants.USER_ID, id);
        editor.apply();
    }

    /**
     * Gets stored user identification.
     * @return user ID string.
     */
    public String getUserId() {
        return prefs.getString(KeyConstants.USER_ID, null);
    }

    /**
     * Enable app to remember user login.
     * @param status true/false.
     */
    public void setRememberMeStatus(boolean status) {
        editor.putBoolean(KeyConstants.REMEMBER_ME_STATUS, status);
        editor.apply();
    }

    /**
     * Gets value for remember user login.
     * @return true/false.
     */
    public boolean getRememberMeStatus() {
        return prefs.getBoolean(KeyConstants.REMEMBER_ME_STATUS, true);
    }

    /**
     * Sets user profile image URL.
     * @param url profile image URL.
     */
    public void setProfilePhotoUrl(String url) {
        editor.putString(KeyConstants.PHOTO, url);
        editor.apply();
    }

    /**
     * Gets user profile image URL.
     * @return profile image URL string.
     */
    public String getProfilePhotoUrl() {
        return prefs.getString(KeyConstants.PHOTO, null);
    }

    /**
     * Sets total saved user information.
     * @param count total profile info saved.
     */
    public void setProfileInfoCount(int count) {
        editor.putInt(KeyConstants.PROFILE_INFO_COUNT, count);
        editor.apply();
    }

    /**
     * Gets total saved user information.
     * @return total saved information.
     */
    public int getProfileInfoCount() {
        return prefs.getInt(KeyConstants.PROFILE_INFO_COUNT, 0);
    }

    /**
     * Stores secret key used to encrypt and decrypt content.
     * @param key encryption/decryption key.
     */
    public void saveSecurityKey(String key) {
        editor.putString(KeyConstants.SECRET_KEY, key);
        editor.apply();
    }

    /**
     * Gets secret key used to encrypt and decrypt content.
     * @return secret security key.
     */
    public String getSecurityKey() {
        return prefs.getString(KeyConstants.SECRET_KEY, null);
    }

    /**
     * Sets user full name.
     * @param fullName user full name.
     */
    public void setProfileName(String fullName) {
        editor.putString(KeyConstants.NAME, fullName);
        editor.apply();
    }

    /**
     * Gets user full name.
     * @return user full name.
     */
    public String getProfileName() {
        return prefs.getString(KeyConstants.NAME, "");
    }

    /**
     * Sets user email address.
     * @param emailAddress user email address.
     */
    public void setProfileEmail(String emailAddress) {
        editor.putString(KeyConstants.EMAIL, emailAddress);
        editor.apply();
    }

    /**
     * Gets user email address.
     * @return email address.
     */
    public String getProfileEmail() {
        return prefs.getString(KeyConstants.EMAIL, "");
    }

    /**
     * Sets user current/physical address.
     * @param physicalAddress user's current location.
     */
    public void setProfileAddress(String physicalAddress) {
        editor.putString(KeyConstants.ADDRESS, physicalAddress);
        editor.apply();
    }

    /**
     * Gets user physical address/current location.
     * @return user current location.
     */
    public String getProfileAddress() {
        return prefs.getString(KeyConstants.ADDRESS, "");
    }

    /**
     * Sets user phone number.
     * @param phoneNumber string.
     */
    public void setPhoneNumber(String phoneNumber) {
        editor.putString(KeyConstants.PHONE, phoneNumber);
        editor.apply();
    }

    /**
     * Gets user registered phone number.
     * @return phone number.
     */
    public String getPhoneNumber() {
        return prefs.getString(KeyConstants.PHONE, "n/a");
    }
}
