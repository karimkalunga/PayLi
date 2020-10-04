package com.media.clouds.app.utils;

/**
 * Holds all app links to the server.
 * Used mostly for networking.
 */
public class URLConstants {
    private static final String BASE_URL = "https://cm.penguinservers.net";
    public static final String LOGIN_URL = BASE_URL.concat("/appsgateway/user_login");
    public static final String SIGN_UP_URL = BASE_URL.concat("/appsgateway/user_register/");
    public static final String SIGN_UP_RESET_PWD_URL = BASE_URL.concat("/appsgateway/user_reset/");
    public static final String AUDIO_CONTENTS_URL = BASE_URL.concat("/appsgateway/contents_audio");
    public static final String LIBRARY_CONTENTS_URL = BASE_URL.concat("/appsgateway/user_library");
    public static final String VIDEO_CONTENTS_URL = BASE_URL.concat("/appsgateway/contents_video");
}
