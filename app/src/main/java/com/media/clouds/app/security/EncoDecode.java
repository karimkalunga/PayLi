package com.media.clouds.app.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.media.clouds.app.dal.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncoDecode {

    private static final String PROVIDER = "BC";
    private static final String KEY_SPEC_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int OUTPUT_KEY_LENGTH = 256;

    private Preferences prefs;
    private static EncoDecode instance = null;

    /**
     * Constructor.
     * @param context application context.
     */
    private EncoDecode(Context context) {
        prefs = Preferences.getInstance(context);
    }

    /**
     * Singleton.
     * @param context application context.
     * @return instance.
     */
    public static synchronized EncoDecode getInstance(Context context) {
        if (instance == null) {
            instance = new EncoDecode(context);
        }
        return instance;
    }

    /**
     * Async task to encrypt audio and video.
     *//*
    private static class EncTask extends AsyncTask<File, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private Context context;

        public EncTask(Context context) {
            this.context = context;
        }

        @SuppressLint("GetInstance")
        @Override
        protected Void doInBackground(File... file) {
            try {
                byte[] data = getSecretKey().getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(data, 0, data.length, KEY_SPEC_ALGORITHM);
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));

                String fileExt = getFileExtension(file[0]);
                File outputFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "tmp.".concat(fileExt));

                byte[] plainBuf = new byte[8192];
                try (FileInputStream in = new FileInputStream(file[0]);
                     FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    int nread;
                    while ((nread = in.read(plainBuf)) > 0) {
                        byte[] enc = cipher.update(plainBuf, 0, nread);
                        outputStream.write(enc);
                    }
                    byte[] enc = cipher.doFinal();
                    outputStream.write(enc);
                    //FileUtils.copyFile(outputFile, file[0]);
                    outputFile.delete();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                Intent intent = new Intent("encryption");
                intent.putExtra("status", true);
                context.sendBroadcast(intent);
            }
            return null;
        }
    }

    *//**
     * Aysnc task to decrypt audio and video.
     *//*
    private static class DecTask extends AsyncTask<File, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private Context context;

        public DecTask(Context context) {
            this.context = context;
        }

        @SuppressLint("GetInstance")
        @Override
        protected Void doInBackground(File... files) {
            try {
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), ivParameterSpec);

                byte[] plainBuf = new byte[8192];
                try (FileInputStream in = new FileInputStream(files[0]);
                     FileOutputStream outputStream = new FileOutputStream(files[1])) {
                    int nread;
                    while ((nread = in.read(plainBuf)) > 0) {
                        byte[] desc = cipher.update(plainBuf, 0, nread);
                        outputStream.write(desc);
                    }
                    byte[] desc = cipher.doFinal();
                    outputStream.write(desc);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                String ext = getFileExtension(files[1]);
                String action = ext.equalsIgnoreCase("mp3") ? "mp3_decryption"
                        : ext.equalsIgnoreCase("mp4") ? "mp4_decryption"
                        : ext.equalsIgnoreCase("pdf") ? "pdf_decryption"
                        : "";
                Intent intent = new Intent(action);
                intent.putExtra("status", true);
                context.sendBroadcast(intent);
            }
            return null;
        }
    }

    public void encode(File fileData, Context context) {
        new EncTask(context).execute(fileData);
    }

    public void decode(File fileData, File tmp, Context context) {
        new DecTask(context).execute(fileData, tmp);
    }

    //todo: Move this function to storage package.
    private String getFileExtension(File file) {
        Uri fileUri = Uri.fromFile(file);
        return MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException {
        String encodedKey = prefs.getSecurityKey();

        if (null == encodedKey || encodedKey.isEmpty()) {
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_SPEC_ALGORITHM);
            keyGenerator.init(OUTPUT_KEY_LENGTH, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            encodedKey = encodeToBase64(secretKey.getEncoded());
            prefs.saveSecurityKey(encodedKey);

            return secretKey;
        }
        byte[] decodedKey = decodeBase64(encodedKey);

        return new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_SPEC_ALGORITHM);
    }*/

    public String encodeToBase64(byte[] plainText)  {
        return Base64.encodeToString(plainText, Base64.DEFAULT);
    }

    public byte[] decodeBase64(String base64)  {
        return Base64.decode(base64, Base64.DEFAULT);
    }
}