package net.opendasharchive.openarchive;

/**
 * Created by micahjlucas on 12/15/14.
 */
public class Globals {

    // EULA
    public static final String ASSET_EULA         = "EULA";
    public static final String PREF_EULA_ACCEPTED = "eula.accepted";

    // request Codes used for media import and capture
    public static final int REQUEST_VIDEO_CAPTURE = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    public static final int REQUEST_AUDIO_CAPTURE = 102;
    public static final int REQUEST_FILE_IMPORT   = 103;

    // intent extras
    public static final String EXTRA_FILE_LOCATION      = "archive_extra_file_location";
    public static final String EXTRA_CURRENT_MEDIA_ID   = "archive_extra_current_media_id";

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

    public final static String SITE_ARCHIVE             = "archive"; //Text, Audio, Photo, Video
}
