package io.scal.openarchive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import io.scal.openarchive.R;

public class ArchiveSettingsActivity extends Activity {
    public static final String TAG = "ArchiveMetadataActivity";
    
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

        //set defaults based on previous selections
        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
        swTitle.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TITLE, true));
		swDescription.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, false));
		swAuthor.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_AUTHOR, false));
		swTags.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TAGS, false));
		swLocation.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_LOCATION, false));
        swUseTor.setChecked(sharedPref.getBoolean(Globals.INTENT_EXTRA_USE_TOR, false));
		rgLicense.check(sharedPref.getInt(Globals.INTENT_EXTRA_LICENSE_URL, R.id.radioByNcNd));
		
		//set up ccLicense link
		final TextView tvCCLicenseLink = (TextView) findViewById(R.id.tv_cc_license);
		tvCCLicenseLink.setMovementMethod(LinkMovementMethod.getInstance());	
    	setCCLicenseText(rgLicense.getCheckedRadioButtonId(), tvCCLicenseLink);
        
        rgLicense.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {	
            	setCCLicenseText(rgLicense.getCheckedRadioButtonId(), tvCCLicenseLink);
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

                final Intent i = getIntent();

                //save defaults for future selections
		        SharedPreferences.Editor editor = sharedPref.edit();
		        editor.putBoolean(Globals.INTENT_EXTRA_SHARE_TITLE, swTitle.isChecked());
		        editor.putBoolean(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, swDescription.isChecked());
		        editor.putBoolean(Globals.INTENT_EXTRA_SHARE_AUTHOR, swAuthor.isChecked());
		        editor.putBoolean(Globals.INTENT_EXTRA_SHARE_TAGS, swTags.isChecked());
		        editor.putBoolean(Globals.INTENT_EXTRA_SHARE_LOCATION, swLocation.isChecked());
                editor.putBoolean(Globals.INTENT_EXTRA_USE_TOR, swUseTor.isChecked());
		        editor.putInt(Globals.INTENT_EXTRA_LICENSE_URL, licenseId);
		        editor.apply();

		        //store data to send with intent
                /*
                i.putExtra(Globals.INTENT_EXTRA_SHARE_TITLE, swTitle.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, swDescription.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_SHARE_AUTHOR, swAuthor.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_SHARE_TAGS, swTags.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_SHARE_LOCATION, swLocation.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_USE_TOR, swUseTor.isChecked());
                i.putExtra(Globals.INTENT_EXTRA_LICENSE_URL, licenseUrl);
				setResult(Activity.RESULT_OK, i);*/
				
				finish();
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
}
