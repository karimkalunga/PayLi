package com.media.clouds.app.utils;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Validator.class
 * This class validates inputs before processing.
 * For example; Full name, email address etc.
 */
public class Validator {
    private static Validator instance = null;

    /**
     * Constructor.
     */
    private Validator() {}

    /**
     * Singleton.
     * @return class instance.
     */
    public static synchronized Validator getInstance() {
        if (instance == null) {
            instance = new Validator();
        }
        return instance;
    }

    /**
     * Validates phone number.
     *
     * Checks whether phone number is empty, null or has 12 digits.
     *
     * @param phoneNumber phone number with country code.
     * @return true/false.
     */
    private boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && !TextUtils.isEmpty(phoneNumber)
                && phoneNumber.length() >= 12;
    }

    /**
     * Validates Sign In params.
     *
     * Checks whether JSON object contains key i.e. phone, password and FCM token Id;
     * Checks whether JSON object has empty values;
     *
     * @param params user Sign In params.
     * @return true/false.
     * @throws Exception JSON exception.
     */
    public boolean isEntryLoginValid(JSONObject params) throws Exception {
        if (!params.has(KeyConstants.PHONE)
                || !params.has(KeyConstants.PASSWORD)
                || !params.has(KeyConstants.FCMTOKEN)) {
            return false;
        }
        if (TextUtils.isEmpty(params.getString(KeyConstants.PASSWORD))) {
            return false;
        }
        return validatePhoneNumber(params.getString(KeyConstants.PHONE));
    }

    /**
     * Validates Sign Up params i.e. phone number.
     *
     * Checks whether JSON object contains key i.e. phone.
     * Checks whether phone number is valid.
     *
     * @param params user Sign Up params.
     * @return true/false.
     * @throws Exception JSON exception.
     */
    public boolean isEntryRegisterPhoneNumberValid(JSONObject params) throws Exception {
        return params.has(KeyConstants.PHONE)
                && validatePhoneNumber(params.getString(KeyConstants.PHONE));
    }

    /**
     * Validates Sign Up params i.e. phone number and OTP.
     *
     * Checks whether JSON object contains key i.e. phone and otp.
     * Checks whether phone number is valid.
     * Checks whether OTP has 4 digits.
     *
     * @param params user Sign Up params i.e. phone number and OTP.
     * @return true/false.
     * @throws Exception JSON exception.
     */
    public boolean isEntryRegisterOTPValid(JSONObject params) throws Exception {
        return params.has(KeyConstants.PHONE)
                && params.has(KeyConstants.CODE)
                && params.getString(KeyConstants.CODE).length() == 4
                && validatePhoneNumber(params.getString(KeyConstants.PHONE));
    }

    /**
     * Validates Sign Up params i.e. phone number and password.
     *
     * Checks whether JSON object contains key i.e. phone and password.
     * Checks whether phone number is valid.
     * Checks whether OTP has 6 or more digits.
     *
     * @param params user Sign Up params i.e. phone number and password.
     * @return true/false.
     * @throws Exception SON exception.
     */
    public boolean isEntryRegisterPasswordValid(JSONObject params) throws Exception {
        return params.has(KeyConstants.PHONE)
                && params.has(KeyConstants.PASSWORD)
                && params.has(KeyConstants.FCMTOKEN)
                && params.getString(KeyConstants.PASSWORD).length() >= 8
                && validatePhoneNumber(params.getString(KeyConstants.PHONE));
    }

    /**
     * Validates content data when purchasing.
     *
     * Checks whether JSON object contains valid keys.
     * Checks for empty strings of data.
     * Checks whether phone number is valid.
     *
     * @param params content data.
     * @return true/false.
     * @throws Exception SON exception.
     */
    public boolean isContentPurchaseDataValid(JSONObject params) throws Exception {
        return params.has(KeyConstants.MSISDN)
                && params.has(KeyConstants.USER_ID)
                && params.has(KeyConstants.CONTENT_ID)
                && params.has(KeyConstants.AMOUNT)
                && params.has(KeyConstants.MNO)
                && !TextUtils.isEmpty(params.getString(KeyConstants.USER_ID))
                && !TextUtils.isEmpty(params.getString(KeyConstants.CONTENT_ID))
                && !TextUtils.isEmpty(params.getString(KeyConstants.AMOUNT))
                && !TextUtils.isEmpty(params.getString(KeyConstants.MNO))
                && validatePhoneNumber(params.getString(KeyConstants.MSISDN));
    }
}
