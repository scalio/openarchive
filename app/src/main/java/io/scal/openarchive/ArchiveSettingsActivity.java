package io.scal.openarchive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import io.scal.openarchive.db.Media;
import io.scal.openarchive.db.MediaMetadata;


public class ArchiveSettingsActivity extends Activity {
    public static final String TAG = "ArchiveMetadataActivity";

    private Context mContext = this;
    private Media mMedia;

    private TextView tvTitleValue;
    private TextView tvDescriptionValue;
    private TextView tvAuthorValue;
    private TextView tvTagsValue;
    private TextView tvLocationValue;

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

        // get current media
        final long mediaId = getIntent().getLongExtra(Globals.EXTRA_CURRENT_MEDIA_ID, 0);
        // init listeners for textviews
        initViews(mediaId);

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
        mMedia = Media.findById(Media.class, mediaId);

        // instantiate values
        tvTitleValue = (TextView) findViewById(R.id.tv_title_value);
        tvDescriptionValue = (TextView) findViewById(R.id.tv_description_value);
        tvAuthorValue = (TextView) findViewById(R.id.tv_author_value);
        tvTagsValue = (TextView) findViewById(R.id.tv_tags_value);
        tvLocationValue = (TextView) findViewById(R.id.tv_location_value);

        if(mediaId == 0) {
            tvTitleValue.setVisibility(View.GONE);
            tvDescriptionValue.setVisibility(View.GONE);
            tvAuthorValue.setVisibility(View.GONE);
            tvTagsValue.setVisibility(View.GONE);
            tvLocationValue.setVisibility(View.GONE);

            return;
        } else {
            tvTitleValue.setVisibility(View.VISIBLE);
            tvDescriptionValue.setVisibility(View.VISIBLE);
            tvAuthorValue.setVisibility(View.VISIBLE);
            tvTagsValue.setVisibility(View.VISIBLE);
            tvLocationValue.setVisibility(View.VISIBLE);

            // set onClick listeners
            tvTitleValue.setOnClickListener(editValueClick);
            tvDescriptionValue.setOnClickListener(editValueClick);
            tvAuthorValue.setOnClickListener(editValueClick);
            tvTagsValue.setOnClickListener(editValueClick);
            tvLocationValue.setOnClickListener(editValueClick);
        }

        // get values
        List<MediaMetadata> mediaMetadataList = MediaMetadata.find(MediaMetadata.class, "media = ?", new String[]{mMedia.getId().toString()});

        // iterate over metadata and retrieve values
        TextView tvCurrent = null;
        for (MediaMetadata mm : mediaMetadataList) {
            long metadataId = mm.getMetadata().getId();

            if (metadataId == 1) {
                tvCurrent = tvTitleValue;
            }
            else if (metadataId == 2) {
                tvCurrent = tvDescriptionValue;
            }
            else if (metadataId == 3) {
                tvCurrent = tvAuthorValue;
            }
            else if (metadataId == 4) {
                tvCurrent = tvLocationValue;
            }
            else if (metadataId == 5) {
                tvCurrent = tvTagsValue;
            }
            tvCurrent.setText(mm.getValue());
        }
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
        List<MediaMetadata> mediaMetadataList = MediaMetadata.find(MediaMetadata.class, "media = ?", new String[]{mMedia.getId().toString()});

        TextView tvCurrent = null;
        // iterate over metadata and store values in db
        for (MediaMetadata mm : mediaMetadataList) {
            long metadataId = mm.getMetadata().getId();
            String value = "";

            if (metadataId == 1) {
                tvCurrent = tvTitleValue;
            }
            else if (metadataId == 2) {
                tvCurrent = tvDescriptionValue;
            }
            else if (metadataId == 3) {
                tvCurrent = tvAuthorValue;
            }
            else if (metadataId == 4) {
                tvCurrent = tvLocationValue;
            }
            else if (metadataId == 5) {
                tvCurrent = tvTagsValue;
            }

            if(null != tvCurrent) {
                value = tvCurrent.getText().toString().trim();
            }

            mm.setValue(value);
            mm.save();
        }
    }
}