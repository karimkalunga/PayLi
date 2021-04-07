package com.media.clouds.app.utils;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator.class
 *
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

    /**
     * Validates profile image data during profile update.
     *
     * Checks whether JSON object contains valid keys.
     * Checks for empty strings of data.
     *
     * @param param profile image data.
     * @return true/false.
     * @throws Exception JSON Exception.
     */
    public boolean isProfileImgContentValid(JSONObject param) throws Exception {
        return param.has(KeyConstants.PHOTO)
                && param.has(KeyConstants.USER_ID)
                && !TextUtils.isEmpty(param.getString(KeyConstants.PHOTO))
                && !TextUtils.isEmpty(param.getString(KeyConstants.USER_ID));
    }

    /**
     * Validates input using regular expression.
     *
     * @param input to be validated i.e. name, email, etc.
     * @param pattern regex pattern.
     * @return true/false.
     */
    private boolean validateByRegex(String input, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(input);
        return matcher.matches();
    }

    /**
     * Validates user full name.
     *
     * Checks whether JSON object contains valid keys.
     * Checks for empty strings of data.
     * Checks whether name is in valid format.
     *
     * @param param user name data.
     * @return true/false.
     * @throws Exception JSON Exception.
     */
    public boolean isUpdateNameContentValid(JSONObject param) throws Exception {
        final String nameRegex = "^[a-zA-z ]*$";
        return param.has(KeyConstants.NAME)
                && param.has(KeyConstants.USER_ID)
                && !TextUtils.isEmpty(param.getString(KeyConstants.NAME))
                && !TextUtils.isEmpty(param.getString(KeyConstants.USER_ID))
                && validateByRegex(param.getString(KeyConstants.NAME), nameRegex);
    }

    /**
     * Validates user email address.
     *
     * Checks whether JSON object contains valid keys.
     * Checks for empty strings of data.
     * Checks whether email address is in valid format.
     *
     * @param param email address data.
     * @return true/false.
     * @throws Exception JSON Exception.
     */
    public boolean isUpdateEmailContentValid(JSONObject param) throws Exception {
        final String emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return param.has(KeyConstants.EMAIL)
                && param.has(KeyConstants.USER_ID)
                && !TextUtils.isEmpty(param.getString(KeyConstants.EMAIL))
                && !TextUtils.isEmpty(param.getString(KeyConstants.USER_ID))
                && validateByRegex(param.getString(KeyConstants.EMAIL), emailRegex);
    }

    /**
     * Validates user physical location/address during profile update.
     *
     * @param param physical location/address data.
     * @return true/false.
     * @throws Exception JSON Exception.
     */
    public boolean isUpdateAddressContentValid(JSONObject param) throws Exception {
        return param.has(KeyConstants.ADDRESS)
                && param.has(KeyConstants.USER_ID)
                && !TextUtils.isEmpty(param.getString(KeyConstants.ADDRESS))
                && !TextUtils.isEmpty(param.getString(KeyConstants.USER_ID));
    }
}
