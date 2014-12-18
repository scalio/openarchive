package io.scal.openarchive;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import io.scal.openarchive.EulaActivity.OnEulaAgreedTo;
import io.scal.openarchive.server.ArchiveLoginActivity;

/**
 * Prompt the user to view & agree to the StoryMaker TOS / EULA
 * and present the choice to create a StoryMaker Account.
 * <p/>
 * Should be launched as the start of a new Task, because
 * when this Activity finishes without starting another,
 * it is the result of the user rejecting the TOS / EULA,
 * and so should result in the app exiting.
 *
 * @author micahjlucas
 */
public class FirstStartActivity extends Activity implements OnEulaAgreedTo {

    private static final String TAG = "FirstStartActivity";

    private boolean mTosAccepted;
    private Button mTosButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first_start);
        mTosAccepted = false;
        mTosButton = (Button) findViewById(R.id.btnTos);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(Globals.PREFERENCES_WP_REGISTERED, false)) {
            // The user is returning to this Activity after a successful WordPress signup
            Intent homeIntent = new Intent(FirstStartActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        }
    }

    /**
     * When the EULA / TOS button is clicked, show the EULA if it hasn't been shown.
     * Else, allow the user to accept immediately.
     */
    public void onTosButtonClick(View v) {
        mTosAccepted = new EulaActivity(this).show();
        if (mTosAccepted) {
            markTosButtonAccepted();
        }
    }

    public void onSignupButtonClick(View v) {
        if (assertTosAccepted()) {
            Intent loginIntent = new Intent(this, MainActivity.class);
            //put flag for signup
            loginIntent.putExtra("url_code", 1);
            startActivity(loginIntent);
        }
    }

    public void onLoginButtonClick(View v) {
        if (assertTosAccepted()) {
            Intent loginIntent = new Intent(this, MainActivity.class);
            //put flag for login
            loginIntent.putExtra("url_code", 0);
            startActivity(loginIntent);
        }
    }

    /**
     * Show an AlertDialog prompting the user to
     * accept the EULA / TOS
     *
     * @return
     */
    private boolean assertTosAccepted() {
        if (!mTosAccepted) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.tos_dialog_title))
                    .setMessage(getString(R.string.tos_dialog_msg))
                    .setPositiveButton(getString(R.string.tos_dialog_positive_button),
                            new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.dismiss();
                                }

                            }).show();
            return false;
        }
        return true;
    }

    private void markTosButtonAccepted() {
        Drawable tosStateDrawable = FirstStartActivity.this.getResources().getDrawable(R.drawable.ic_contextsm_checkbox_checked);
        mTosButton.setCompoundDrawablesWithIntrinsicBounds(tosStateDrawable, null, null, null);
    }

    @Override
    public void onEulaAgreedTo() {
        mTosAccepted = true;
        markTosButtonAccepted();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult, requestCode:" + requestCode + ", resultCode: " + resultCode);

        String path = null;
        if (resultCode == RESULT_OK) {
            if(requestCode == Globals.REQUEST_VIDEO_CAPTURE) {
                Uri uri = intent.getData();
                path = getRealPathFromURI(getApplicationContext(), uri);
                Log.d(TAG, "onActivityResult, video path:" + path);

            } else if(requestCode == Globals.REQUEST_IMAGE_CAPTURE) {
                path = this.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString(Globals.EXTRA_FILE_LOCATION, null);
                Log.d(TAG, "onActivityResult, path:" + path);

            } else if(requestCode == Globals.REQUEST_AUDIO_CAPTURE) {
                Uri uri = intent.getData();
                path = getRealPathFromURI(getApplicationContext(), uri);
                Log.d(TAG, "onActivityResult, audio path:" + path);

            } else if (requestCode == Globals.REQUEST_FILE_IMPORT) {
                Uri uri = intent.getData();
                // Will only allow stream-based access to files
                if (Build.VERSION.SDK_INT >= 19) {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                path = getRealPathFromURI(getApplicationContext(), uri);
                Log.d(TAG, "onActivityResult, imported file path:" + path);
            }

            if (null == path) {
                Intent viewMediaIntent = new Intent(this, ReviewMediaActivity.class);
                startActivity(viewMediaIntent);
            } else {
                Log.d(TAG, "onActivityResult: Invalid file on import or capture");
                Toast.makeText(getApplicationContext(), R.string.error_on_activity_result, Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}

