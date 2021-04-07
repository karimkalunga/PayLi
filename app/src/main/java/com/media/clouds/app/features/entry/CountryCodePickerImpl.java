package com.media.clouds.app.features.entry;

import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * CountryCodePickerImpl.class
 * This class handles CCP library custom implementations.
 */
public class CountryCodePickerImpl {
    private static CountryCodePickerImpl instance = null;

    private CountryCodePickerImpl() {}

    /**
     * Singleton initialization.
     * @return CCPImpl instance.
     */
    public static synchronized CountryCodePickerImpl getInstance() {
        if (instance == null) {
            instance = new CountryCodePickerImpl();
        }
        return instance;
    }

    /**
     * Extracts phone number and country code from CPP library.
     * @param phoneNumber country code and national phone number.
     * @return phone number with country code.
     */
    public String getPhoneNumberFromCPP(Phonenumber.PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            int countryCode = phoneNumber.getCountryCode();
            long nationalNumber = phoneNumber.getNationalNumber();
            return String.valueOf(countryCode).concat(String.valueOf(nationalNumber));
        }
        return "";
    }
}
