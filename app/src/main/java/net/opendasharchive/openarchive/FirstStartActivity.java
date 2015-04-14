package net.opendasharchive.openarchive;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import net.opendasharchive.openarchive.EulaActivity.OnEulaAgreedTo;
import io.scal.secureshareui.controller.ArchiveSiteController;
import io.scal.secureshareui.controller.SiteController;
import io.scal.secureshareui.model.Account;

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

    private SiteController.OnEventListener mAuthEventListener = new SiteController.OnEventListener() {

        @Override
        public void onSuccess(Account account) {
            account.saveToSharedPrefs(FirstStartActivity.this, null);
        }

        @Override
        public void onFailure(Account account, String failureMessage) {
            // TODO we should invalidate the locally saved credentials rather than just clearing them
            account.setCredentials(null);
            account.setIsConnected(false);
            account.saveToSharedPrefs(FirstStartActivity.this, null);
        }

        @Override
        public void onRemove(Account account) {
            Account.clearSharedPreferences(FirstStartActivity.this, null);
            // FIXME do we need to do somehting to clear cookies in the webview? or is that handled for us?
        }
    };

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
            SiteController siteController = SiteController.getSiteController(ArchiveSiteController.SITE_KEY, this, null, null);
            siteController.setOnEventListener(mAuthEventListener);
            Account account = new Account(this, null);
            siteController.startAuthentication(account);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent); // FIXME do we really need to call up to the super?
        if (requestCode == SiteController.CONTROLLER_REQUEST_CODE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                Account mAccount = new Account(this, null);
                String credentials = intent.getStringExtra(SiteController.EXTRAS_KEY_CREDENTIALS);
                mAccount.setCredentials(credentials != null ? credentials : "");

                String username = intent.getStringExtra(SiteController.EXTRAS_KEY_USERNAME);
                mAccount.setUserName(username != null ? username : "");

                String data = intent.getStringExtra(SiteController.EXTRAS_KEY_DATA);
                mAccount.setData(data != null ? data : null);

                mAccount.setAreCredentialsValid(true);
                mAccount.saveToSharedPrefs(this, null);

                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);

            } else {
                Toast.makeText(this, getString(R.string.problem_authticating), Toast.LENGTH_LONG).show();
            }
        }
    }
}

