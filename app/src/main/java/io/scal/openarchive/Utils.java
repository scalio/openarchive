package io.scal.openarchive;

/**
 * Created by micahjlucas on 12/16/14.
 */

import java.security.SecureRandom;
import java.util.Random;

//import info.guardianproject.onionkit.ui.OrbotHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Toast;

import io.scal.openarchive.database.MetadataTable;
import io.scal.openarchive.database.OpenArchiveContentProvider;

public class Utils {

    // netcipher
    public static final String ORBOT_HOST = "127.0.0.1";
    public static final int ORBOT_HTTP_PORT = 8118;
    public static final int ORBOT_SOCKS_PORT = 9050;

    public static boolean isOrbotInstalledAndRunning(Context mContext) {

        //TODO
        //OrbotHelper orbotHelper = new OrbotHelper(mContext);
        //return (orbotHelper.isOrbotInstalled() && orbotHelper.isOrbotRunning());
        return false;
    }

    // TODO audit code for security since we use the to generate random strings for url slugs
    public static final class RandomString
    {

        /* Assign a string that contains the set of characters you allow. */
        private static final String symbols = "abcdefghijklmnopqrstuvwxyz0123456789";

        private final Random random = new SecureRandom();

        private final char[] buf;

        public RandomString(int length)
        {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString()
        {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
            return new String(buf);
        }

    }

    public static String getMediaType(String mediaPath) {
        String result = null;
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(mediaPath);
        result = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        if (result == null) {
            if (mediaPath.endsWith("wav")) {
                result = "audio/wav";
            }
            else if (mediaPath.endsWith("mp3")) {
                result = "audio/mpeg";
            }
            else if (mediaPath.endsWith("3gp")) {
                result = "audio/3gpp";
            }
            else if (mediaPath.endsWith("mp4")) {
                result = "video/mp4";
            }
            else if (mediaPath.endsWith("jpg")) {
                result = "image/jpeg";
            }
            else if (mediaPath.endsWith("png")) {
                result = "image/png";
            }
        }

        if (result.contains("audio")) {
            return "audio";
        } else if(result.contains("image")) {
            return "image";
        } else if(result.contains("video")) {
            return "movies";
        }

        return null;
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

    //called the first time the app runs to add values to the db
    public static void initDB(Context context) {
        Uri uri = OpenArchiveContentProvider.Metadata.METADATA;

        String[] defaultValues = {"Use Tor", "Title", "Description", "Author", "Location", "Tags"};

        for (String value : defaultValues) {
            ContentValues metadataValues = new ContentValues();
            metadataValues.put(MetadataTable.name, value);
            context.getContentResolver().insert(uri, metadataValues);
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

