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

    // database
    public static final int DATABASE_VERSION = 1;

    // request Codes used for media import and capture
    public static final int REQUEST_VIDEO_CAPTURE = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    public static final int REQUEST_AUDIO_CAPTURE = 102;
    public static final int REQUEST_FILE_IMPORT   = 103;

    // intent extras used for external Media Capture
    public static final String EXTRA_PATH_ID         = "EXTRA_PATH_ID";
    public static final String EXTRA_FILE_LOCATION   = "FILE_LOCATION";
    public static final String PREFS_CALLING_CARD_ID = "PREFS_CALLING_CARD_ID";

    // metadata prefs
    public static final String PREF_FILE_KEY         = "archive_metadata_key";

    public static final String INTENT_EXTRA_MEDIA_ID = "archive-media-id";
    public static final String INTENT_EXTRA_USE_TOR = "archive-use-tor";
    public static final String INTENT_EXTRA_SHARE_TITLE = "archive-share-title";
    public static final String INTENT_EXTRA_SHARE_DESCRIPTION = "archive-share-description";
    public static final String INTENT_EXTRA_SHARE_AUTHOR = "archive-share-author";
    public static final String INTENT_EXTRA_SHARE_TAGS = "archive-share-tags";
    public static final String INTENT_EXTRA_SHARE_LOCATION = "archive-share-location";
    public static final String INTENT_EXTRA_LICENSE_URL = "archive-share-license-url";

    public static final String INTENT_CURRENT_MEDIA_PATH = "archive-current-media-path";

    // intent extras used for login/signup
    public static final String EXTRA_ACCESS_KEY     = "EXTRA_ACCESS_KEY";
    public static final String EXTRA_SECRET_KEY     = "EXTRA_SECRET_KEY";

    // medium values
    public static final String AUDIO = "audio"; //TODO may be Audio instead of audio
    public static final String PHOTO = "photo";
    public static final String VIDEO = "video";

    public final static String SITE_ARCHIVE = "archive"; //Text, Audio, Photo, Video
}
