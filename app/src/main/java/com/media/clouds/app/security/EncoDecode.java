package com.media.clouds.app.security;

import android.util.Base64;

public class EncoDecode {

    private static EncoDecode instance = null;
    /**
     * Constructor.
     */
    private EncoDecode() {}

    /**
     * Singleton.
     * @return instance.
     */
    public static synchronized EncoDecode getInstance() {
        if (instance == null) {
            instance = new EncoDecode();
        }
        return instance;
    }

    /**
     * Encodes bytes of data to base64.
     * @param plainText in bytes.
     * @return base64 string.
     */
    public String encodeToBase64(byte[] plainText)  {
        return Base64.encodeToString(plainText, Base64.DEFAULT);
    }

    /**
     * Decodes base64 string.
     * @param base64 string.
     * @return plain text bytes of data.
     */
    public byte[] decodeBase64(String base64)  {
        return Base64.decode(base64, Base64.DEFAULT);
    }
}