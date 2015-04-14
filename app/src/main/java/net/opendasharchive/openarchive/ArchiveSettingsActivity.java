package net.opendasharchive.openarchive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import net.opendasharchive.openarchive.db.Media;

import java.util.HashMap;

import io.scal.secureshareui.controller.SiteController;
import io.scal.secureshareui.lib.ArchiveMetadataActivity;
import io.scal.secureshareui.lib.Util;


public class ArchiveSettingsActivity extends Activity {
    public static final String TAG = "ArchiveMetadataActivity";

    private Context mContext = this;
    private Media mMedia;

    private Switch swTitle;
    private Switch swDescription;
    private Switch swAuthor;
    private Switch swTags;
    private Switch swLocation;
    private Switch swUseTor;
    private RadioGroup rgLicense;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvAuthor;
    private TextView tvTags;
    private TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_metadata);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        swTitle = (Switch) findViewById(R.id.sw_title);
        swDescription = (Switch) findViewById(R.id.sw_description);
        swAuthor = (Switch) findViewById(R.id.sw_author);
        swTags = (Switch) findViewById(R.id.sw_tags);
        swLocation = (Switch) findViewById(R.id.sw_location);
        swUseTor = (Switch) findViewById(R.id.sw_use_tor);
        rgLicense = (RadioGroup) findViewById(R.id.radioGroupCC);

        // set defaults based on previous selections
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveMediaMetadata();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveMediaMetadata();
        super.onBackPressed();
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

    // TODO helper method to grab the shared prefs and return a values hashmap

    // TODO this also needs to store what the sharing prefs were when this was submitted I believe
    private void saveMediaMetadata() {
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
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Globals.PREF_SHARE_TITLE, swTitle.isChecked());
        editor.putBoolean(Globals.PREF_SHARE_DESCRIPTION, swDescription.isChecked());
        editor.putBoolean(Globals.PREF_SHARE_AUTHOR, swAuthor.isChecked());
        editor.putBoolean(Globals.PREF_SHARE_LOCATION, swLocation.isChecked());
        editor.putBoolean(Globals.PREF_SHARE_TAGS, swTags.isChecked());
        editor.putBoolean(Globals.PREF_USE_TOR, swUseTor.isChecked());
        editor.putInt(Globals.PREF_LICENSE_URL, licenseId); // FIXME this should store the license url not the idx
        editor.apply();

        // save value changes in db
        if(null != mMedia) {
            // set values
            mMedia.setTitle(tvTitle.getText().toString().trim());
            mMedia.setDescription(tvDescription.getText().toString().trim());
            mMedia.setAuthor(tvAuthor.getText().toString().trim());
            mMedia.setLocation(tvLocation.getText().toString().trim());
            mMedia.setTags(tvTags.getText().toString().trim());
            mMedia.setUseTor(swUseTor.isChecked());

            mMedia.save();
        }
    }

    public static String getSlug(String title) {
        // FIXME only do this if they are sharing title, otherwise use a random string of chars
        return title.replace(' ', '-'); // FIXME need a real, i18n aware slug algorithm -- http://docs.aws.amazon.com/AmazonS3/latest/UG/CreatingaBucket.html
    }

    public static HashMap<String, String> getMediaMetadata(Context context, Media mMedia) {
        HashMap<String, String> valueMap = new HashMap<String, String>();
        SharedPreferences sharedPref = context.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        boolean isTorUsed = true;
        boolean isTitleShared = true;
        boolean isTagsShared = true;
        boolean isAuthorShared = true;
        boolean isLocationShared = true;
        boolean isDescriptionShared = true;

        String path = Util.getPath(context, Uri.parse(mMedia.getOriginalFilePath()));

        valueMap.put(SiteController.VALUE_KEY_MEDIA_PATH, path);
        valueMap.put(SiteController.VALUE_KEY_USE_TOR, isTorUsed ? "true" : "false");
        valueMap.put(SiteController.VALUE_KEY_LICENSE_URL, "https://creativecommons.org/licenses/by/4.0/"); // TODO
        valueMap.put(SiteController.VALUE_KEY_SLUG, getSlug(mMedia.getTitle()));

        valueMap.put(SiteController.VALUE_KEY_TITLE, mMedia.getTitle());
        valueMap.put(ArchiveMetadataActivity.INTENT_EXTRA_SHARE_TITLE, isTitleShared ? "true" : "false");

        String tags = context.getString(R.string.default_tags) + ";" + mMedia.getTags(); // FIXME are keywords/tags separated by spaces or commas?
        valueMap.put(SiteController.VALUE_KEY_TAGS, tags);
        valueMap.put(ArchiveMetadataActivity.INTENT_EXTRA_SHARE_TAGS, isTagsShared ? "true" : "false");

        valueMap.put(SiteController.VALUE_KEY_AUTHOR, mMedia.getAuthor());
        valueMap.put(ArchiveMetadataActivity.INTENT_EXTRA_SHARE_AUTHOR, isAuthorShared ? "true" : "false");

        valueMap.put(SiteController.VALUE_KEY_PROFILE_URL, "TESTING"); // TODO

        valueMap.put(SiteController.VALUE_KEY_LOCATION_NAME, "TESTING"); // TODO
        valueMap.put(ArchiveMetadataActivity.INTENT_EXTRA_SHARE_LOCATION, isLocationShared ? "true" : "false");

        valueMap.put(SiteController.VALUE_KEY_BODY, mMedia.getDescription());
        valueMap.put(ArchiveMetadataActivity.INTENT_EXTRA_SHARE_DESCRIPTION, isDescriptionShared ? "true" : "false");

        return valueMap;
    }
}