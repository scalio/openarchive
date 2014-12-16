package io.scal.openarchive;

/**
 * Created by micahjlucas on 12/15/14.
 */
public class Globals {

    // EULA
    public static final String ASSET_EULA = "EULA";
    public static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
    public static final String PREFERENCES_EULA = "eula";
    public static final String PREFERENCES_ANALYTICS = "analytics";
    public static final String PREFERENCES_WP_REGISTERED = "wp.registered";

    // request Codes used for media import and capture
    public static final int REQUEST_VIDEO_CAPTURE = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    public static final int REQUEST_AUDIO_CAPTURE = 102;
    public static final int REQUEST_FILE_IMPORT   = 103;

    // intent extras used for external Media Capture
    public static final String EXTRA_PATH_ID         = "EXTRA_PATH_ID";
    public static final String EXTRA_FILE_LOCATION   = "FILE_LOCATION";
    public static final String PREFS_CALLING_CARD_ID = "PREFS_CALLING_CARD_ID";

    // medium values
    public static final String AUDIO = "audio";
    public static final String PHOTO = "photo";
    public static final String VIDEO = "video";
}
