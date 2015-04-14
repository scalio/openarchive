package net.opendasharchive.openarchive;

/**
 * Created by micahjlucas on 12/16/14.
 */

import java.io.File;

//import info.guardianproject.onionkit.ui.OrbotHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Toast;

import net.opendasharchive.openarchive.db.Media;

public class Utility {

    public static Media.MEDIA_TYPE getMediaType(String mediaPath) {
        // makes comparisons easier
        mediaPath = mediaPath.toLowerCase();

        String result;
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(mediaPath);
        result = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        if (result == null) {
            if (mediaPath.endsWith("wav")) {
                result = "audio/wav";
            } else if (mediaPath.toLowerCase().endsWith("mp3")) {
                result = "audio/mpeg";
            } else if (mediaPath.endsWith("3gp")) {
                result = "audio/3gpp";
            } else if (mediaPath.endsWith("mp4")) {
                result = "video/mp4";
            } else if (mediaPath.endsWith("jpg")) {
                result = "image/jpeg";
            } else if (mediaPath.endsWith("png")) {
                result = "image/png";
            } else {
                // for imported files
                result = mediaPath;
            }
        }

        if (result.contains("audio")) {
            return Media.MEDIA_TYPE.AUDIO;
        } else if(result.contains("image")) {
            return Media.MEDIA_TYPE.IMAGE;
        } else if(result.contains("video")) {
            return Media.MEDIA_TYPE.VIDEO;
        }

        return null;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri == null) {
            return null;
        }

        // work-around to handle normal paths
        if (contentUri.toString().startsWith(File.separator)) {
            return contentUri.toString();
        }

        // work-around to handle normal paths
        if (contentUri.toString().startsWith("file://")) {
            return contentUri.toString().split("file://")[1];
        }

        // TODO deal with document providers
        // path of form : content://com.android.providers.media.documents/document/video:183

        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void clearWebviewAndCookies(WebView webview, Activity activity) {
        CookieSyncManager.createInstance(activity);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        if(webview != null) {
            webview.clearHistory();
            webview.clearCache(true);
            webview.clearFormData();
            webview.loadUrl("about:blank");
            webview.destroy();
        }
    }

    public static boolean stringNotBlank(String string) {
        return (string != null) && !string.equals("");
    }

    public static String stringArrayToCommaString(String[] strings) {
        if (strings.length > 0) {
            StringBuilder nameBuilder = new StringBuilder();

            for (String n : strings) {
                nameBuilder.append(n.replaceAll("'", "\\\\'")).append(",");
            }

            nameBuilder.deleteCharAt(nameBuilder.length() - 1);

            return nameBuilder.toString();
        } else {
            return "";
        }
    }

    public static String[] commaStringToStringArray(String string) {
        if (string != null) {
            return string.split(",");
        } else {
            return null;
        }
    }

    public static void toastOnUiThread(Activity activity, String message) {
        toastOnUiThread(activity, message, false);
    }

    public static void toastOnUiThread(Activity activity, String message, final boolean isLongToast) {
        final Activity _activity = activity;
        final String _msg = message;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(_activity.getApplicationContext(), _msg, isLongToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toastOnUiThread(FragmentActivity fragmentActivity, String message) {
        toastOnUiThread(fragmentActivity, message, false);
    }

    public static void toastOnUiThread(FragmentActivity fragmentActivity, String message, final boolean isLongToast) {
        final FragmentActivity _activity = fragmentActivity;
        final String _msg = message;
        fragmentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(_activity.getApplicationContext(), _msg, isLongToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }
}