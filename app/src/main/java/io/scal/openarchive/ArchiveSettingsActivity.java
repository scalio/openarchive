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
import io.scal.openarchive.server.SiteController;
import io.scal.openarchive.R;

public class ArchiveSettingsActivity extends Activity {
    public static final String TAG = "ArchiveMetadataActivity";
    
    public static final String INTENT_EXTRA_SHARE_TITLE = "archive-share-title";
    public static final String INTENT_EXTRA_SHARE_DESCRIPTION = "archive-share-description";
    public static final String INTENT_EXTRA_SHARE_AUTHOR = "archive-share-author";
    public static final String INTENT_EXTRA_SHARE_TAGS = "archive-share-tags";
    public static final String INTENT_EXTRA_SHARE_LOCATION = "archive-share-location";
    public static final String INTENT_EXTRA_LICENSE_URL = "archive-share-license-url";
    
    public static final String PREF_FILE_KEY = "archive_metadata_key";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive_metadata);
		Button btnSave = (Button) findViewById(R.id.btnSave);

        final Switch title = (Switch) findViewById(R.id.tb_title);
        final Switch description = (Switch) findViewById(R.id.tb_description);
        final Switch author = (Switch) findViewById(R.id.tb_author);
        final Switch tags = (Switch) findViewById(R.id.tb_tags);
        final Switch location = (Switch) findViewById(R.id.tb_location);
        final RadioGroup rgLicense = (RadioGroup) findViewById(R.id.radioGroupCC);

        final Intent i = getIntent();
        Bundle extras = new Bundle(); //TODO should be i.getExtras()
        
        //set defaults based on previous selections
        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE); 
		title.setChecked(sharedPref.getBoolean(INTENT_EXTRA_SHARE_TITLE, true));
		description.setChecked(sharedPref.getBoolean(INTENT_EXTRA_SHARE_DESCRIPTION, false));
		author.setChecked(sharedPref.getBoolean(INTENT_EXTRA_SHARE_AUTHOR, false));
		tags.setChecked(sharedPref.getBoolean(INTENT_EXTRA_SHARE_TAGS, false));
		location.setChecked(sharedPref.getBoolean(INTENT_EXTRA_SHARE_LOCATION, false));
		rgLicense.check(sharedPref.getInt(INTENT_EXTRA_LICENSE_URL, R.id.radioByNcNd));
		
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
		        
		        //save defaults for future selections
		        SharedPreferences.Editor editor = sharedPref.edit();
		        editor.putBoolean(INTENT_EXTRA_SHARE_TITLE, title.isChecked());
		        editor.putBoolean(INTENT_EXTRA_SHARE_DESCRIPTION, description.isChecked());
		        editor.putBoolean(INTENT_EXTRA_SHARE_AUTHOR, author.isChecked());
		        editor.putBoolean(INTENT_EXTRA_SHARE_TAGS, tags.isChecked());
		        editor.putBoolean(INTENT_EXTRA_SHARE_LOCATION, location.isChecked());
		        editor.putInt(INTENT_EXTRA_LICENSE_URL, licenseId);
		        editor.apply();
		        
		        //store data to send with intent
                i.putExtra(INTENT_EXTRA_SHARE_TITLE, title.isChecked());
                i.putExtra(INTENT_EXTRA_SHARE_DESCRIPTION, description.isChecked());
                i.putExtra(INTENT_EXTRA_SHARE_AUTHOR, author.isChecked());
                i.putExtra(INTENT_EXTRA_SHARE_TAGS, tags.isChecked());
                i.putExtra(INTENT_EXTRA_SHARE_LOCATION, location.isChecked());
                i.putExtra(INTENT_EXTRA_LICENSE_URL, licenseUrl);
				setResult(Activity.RESULT_OK, i);
				
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
