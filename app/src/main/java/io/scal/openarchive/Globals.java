package io.scal.openarchive;

/**
 * Created by micahjlucas on 12/15/14.
 */
public class Globals {

    // EULA
    public static final String ASSET_EULA               = "EULA";
    public static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
    public static final String PREFERENCES_EULA         = "eula";
    public static final String PREFERENCES_ANALYTICS    = "analytics";

    // request Codes used for media import and capture
    public static final int REQUEST_VIDEO_CAPTURE = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    public static final int REQUEST_AUDIO_CAPTURE = 102;
    public static final int REQUEST_FILE_IMPORT   = 103;

    // netcipher
    public static final String ORBOT_HOST       = "127.0.0.1";
    public static final int ORBOT_HTTP_PORT     = 8118;
    public static final int ORBOT_SOCKS_PORT    = 9050;

    // intent extras
    public static final String EXTRA_FILE_LOCATION      = "archive_extra_file_location";
    public static final String EXTRA_CURRENT_FILE_PATH  = "archive_extra_current_file_path";
    public static final String EXTRA_CURRENT_MEDIA_ID   = "archive_extra_current_media_id";

    public static final String EXTRA_ACCESS_KEY         = "archive_extra_access_key";
    public static final String EXTRA_SECRET_KEY         = "archive_extra_secret_key";

    public static final String EXTRA_VALUE_TITLE        = "archive_extra_value_title";
    public static final String EXTRA_VALUE_DESCRIPTION  = "archive_extra_value_description";
    public static final String EXTRA_VALUE_AUTHOR       = "archive_extra_value_author";
    public static final String EXTRA_VALUE_TAGS         = "archive_extra_value_tags";
    public static final String EXTRA_VALUE_LOCATION     = "archive_extra_value_location";

    public static final String EXTRA_IS_EDIT_MODE       = "archive_extra_is_edit_mode";

    // prefs
    public static final String PREF_FILE_KEY            = "archive_pref_key";

    public static final String PREF_FIRST_RUN           = "archive_pref_first_run";

    public static final String PREF_USE_TOR             = "archive_pref_use_tor";
    public static final String PREF_SHARE_TITLE         = "archive_pref_share_title";
    public static final String PREF_SHARE_DESCRIPTION   = "archive_pref_share_description";
    public static final String PREF_SHARE_AUTHOR        = "archive_pref_share_author";
    public static final String PREF_SHARE_TAGS          = "archive_pref_share_tags";
    public static final String PREF_SHARE_LOCATION      = "archive_pref_share_location";
    public static final String PREF_LICENSE_URL         = "archive_pref_share_license_url";

    // medium values
    public static final String AUDIO = "audio"; //TODO may be Audio instead of audio
    public static final String PHOTO = "photo";
    public static final String VIDEO = "video";

    public final static String SITE_ARCHIVE = "archive"; //Text, Audio, Photo, Video
}
