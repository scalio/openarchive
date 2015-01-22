package io.scal.openarchive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.scal.openarchive.db.Media;
import io.scal.secureshareui.lib.Util;


public class ArchiveSettingsActivity extends Activity {
    public static final String TAG = "ArchiveMetadataActivity";

    private Context mContext = this;
    private Media mMedia;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvAuthor;
    private TextView tvTags;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_metadata);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        final Switch swTitle = (Switch) findViewById(R.id.sw_title);
        final Switch swDescription = (Switch) findViewById(R.id.sw_description);
        final Switch swAuthor = (Switch) findViewById(R.id.sw_author);
        final Switch swTags = (Switch) findViewById(R.id.sw_tags);
        final Switch swLocation = (Switch) findViewById(R.id.sw_location);
        final Switch swUseTor = (Switch) findViewById(R.id.sw_use_tor);
        final RadioGroup rgLicense = (RadioGroup) findViewById(R.id.radioGroupCC);

        // set defaults based on previous selections
        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
        swTitle.setChecked(sharedPref.getBoolean(Globals.PREF_SHARE_TITLE, true));
        swDescription.setChecked(sharedPref.getBoolean(Globals.PREF_SHARE_DESCRIPTION, false));
        swAuthor.setChecked(sharedPref.getBoolean(Globals.PREF_SHARE_AUTHOR, false));
        swTags.setChecked(sharedPref.getBoolean(Globals.PREF_SHARE_TAGS, false));
        swLocation.setChecked(sharedPref.getBoolean(Globals.PREF_SHARE_LOCATION, false));
        swUseTor.setChecked(sharedPref.getBoolean(Globals.PREF_USE_TOR, false));
        rgLicense.check(sharedPref.getInt(Globals.PREF_LICENSE_URL, R.id.radioByNcNd));

        // get current media
        final long mediaId = getIntent().getLongExtra(Globals.EXTRA_CURRENT_MEDIA_ID, -1);
        // init listeners for textviews
        initViews(mediaId);

        // set up ccLicense link
        final TextView tvCCLicenseLink = (TextView) findViewById(R.id.tv_cc_license);
        tvCCLicenseLink.setMovementMethod(LinkMovementMethod.getInstance());
        setCCLicenseText(rgLicense.getCheckedRadioButtonId(), tvCCLicenseLink);

        rgLicense.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCCLicenseText(rgLicense.getCheckedRadioButtonId(), tvCCLicenseLink);
            }
        });

        // set OnClick listeners
        swUseTor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // check if tor is installed and display dialog if not
                    boolean isTorInstalled = Util.checkIsTorInstalledDialog(mContext);

                    // mark tor switched based on returned value
                    buttonView.setChecked(isTorInstalled);
                }
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String licenseUrl = null;
                int licenseId = rgLicense.getCheckedRadioButtonId();
                if (licenseId == R.id.radioBy) {
                    licenseUrl = "https://creativecommons.org/licenses/by/4.0/";
                } else if (licenseId == R.id.radioBySa) {
                    licenseUrl = "https://creativecommons.org/licenses/by-sa/4.0/";
                } else { // ByNcNd is default
                    licenseUrl = "http://creativecommons.org/licenses/by-nc-nd/4.0/";
                }

                // save defaults for future selections
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(Globals.PREF_SHARE_TITLE, swTitle.isChecked());
                editor.putBoolean(Globals.PREF_SHARE_DESCRIPTION, swDescription.isChecked());
                editor.putBoolean(Globals.PREF_SHARE_AUTHOR, swAuthor.isChecked());
                editor.putBoolean(Globals.PREF_SHARE_LOCATION, swLocation.isChecked());
                editor.putBoolean(Globals.PREF_SHARE_TAGS, swTags.isChecked());
                editor.putBoolean(Globals.PREF_USE_TOR, swUseTor.isChecked());
                editor.putInt(Globals.PREF_LICENSE_URL, licenseId);
                editor.apply();

                // save value changes in db
                if(null != mMedia) {
                    saveMediaMetadata();

                    Intent reviewMediaIntent = new Intent(mContext, ReviewMediaActivity.class);
                    reviewMediaIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, mMedia.getId());
                    startActivity(reviewMediaIntent);
                } else {
                    finish();
                }
            }
        });
    }

    private void setCCLicenseText(int licenseId, TextView tvCCLicenseLink) {
        if (licenseId == R.id.radioBy) {
            tvCCLicenseLink.setText(R.string.archive_license_by);
        } else if (licenseId == R.id.radioBySa) {
            tvCCLicenseLink.setText(R.string.archive_license_bysa);
        } else { // ByNcNd is default
            tvCCLicenseLink.setText(R.string.archive_license_byncnd);
        }
    }

    private void initViews(long mediaId) {

        // instantiate values
        tvTitle = (TextView) findViewById(R.id.tv_title_value);
        tvDescription = (TextView) findViewById(R.id.tv_description_value);
        tvAuthor = (TextView) findViewById(R.id.tv_author_value);
        tvTags = (TextView) findViewById(R.id.tv_tags_value);
        tvLocation = (TextView) findViewById(R.id.tv_location_value);

        // if valid media id
        if(mediaId >= 0) {
            mMedia = Media.getMediaById(mediaId);
        } else {
            tvTitle.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
            tvAuthor.setVisibility(View.GONE);
            tvTags.setVisibility(View.GONE);
            tvLocation.setVisibility(View.GONE);

            return;
        }

        // set to visible
        tvTitle.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        tvAuthor.setVisibility(View.VISIBLE);
        tvTags.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.VISIBLE);

        // set values
        tvTitle.setText(mMedia.getTitle());
        tvDescription.setText(mMedia.getDescription());
        tvAuthor.setText(mMedia.getAuthor());
        tvLocation.setText(mMedia.getLocation());
        tvTags.setText(mMedia.getTags());

        // set onClick listeners
        tvTitle.setOnClickListener(editValueClick);
        tvDescription.setOnClickListener(editValueClick);
        tvAuthor.setOnClickListener(editValueClick);
        tvTags.setOnClickListener(editValueClick);
        tvLocation.setOnClickListener(editValueClick);
    }

    private OnClickListener editValueClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
        // set edittext view to get user input
        final EditText etInput = new EditText(mContext);

        // cast corresponding textview and set input
        final TextView tvText = (TextView) v;
        etInput.setText(tvText.getText().toString());

        // set cursor to end of line
        etInput.setSelection(etInput.getText().length());

        new AlertDialog.Builder(mContext)
                .setTitle(R.string.lbl_update_value)
                .setView(etInput)
                .setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = etInput.getText();
                        tvText.setText(value);
                    }
                }).setNegativeButton(R.string.lbl_Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        }).show();
        }
    };

    private void saveMediaMetadata() {
        // set values
        mMedia.setTitle(tvTitle.getText().toString().trim());
        mMedia.setDescription(tvDescription.getText().toString().trim());
        mMedia.setAuthor(tvAuthor.getText().toString().trim());
        mMedia.setLocation(tvLocation.getText().toString().trim());
        mMedia.setTags(tvTags.getText().toString().trim());

        mMedia.save();
    }
}