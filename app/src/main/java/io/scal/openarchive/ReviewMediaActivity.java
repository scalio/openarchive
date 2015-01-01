package io.scal.openarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.scal.openarchive.database.MetadataTable;
import io.scal.openarchive.database.OpenArchiveContentProvider;


public class ReviewMediaActivity extends ActionBarActivity {
    private Context mContext = this;
    private static String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        mFilePath = getIntent().getStringExtra(Constants.INTENT_EXTRA_FILE_PATH);
        init();
    }

    private void init() {
        if (mFilePath == null) {
            Toast.makeText(this, "No Media Found!", Toast.LENGTH_LONG);
            finish();
        }

        Uri uri = OpenArchiveContentProvider.Metadata.METADATA;
        Cursor result = this.getContentResolver().query(uri, new String[] { MetadataTable.id, MetadataTable.name }, null, null, null);

        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageURI(Uri.parse(mFilePath));

        TableLayout tblMediaMetadata = (TableLayout) findViewById(R.id.tblMediaMetadata);
        tblMediaMetadata.removeAllViews();

        boolean[] arPermissions = getMetadataPermissions();

        //TODO
        String[] testDesc = {"Yes", "The Media Title", "This is a description of the media.", "Author Name", "San Francisco, CA", "tag 1, tag 2, tag 3"};
        int i = 0;

        while(result.moveToNext()) {

            //if user has selected not to upload this specific metadata
            if(!arPermissions[i]) {
                i++;
                continue;
            }

            String label = result.getString(1);
            String desc = testDesc[i++];

            View vRow = getLayoutInflater().inflate(R.layout.row_media_metadata, null);

            TextView tvRowLabel = (TextView) vRow.findViewById(R.id.tvRowLabel);
            tvRowLabel.setText(label);

            TextView tvRowDesc = (TextView) vRow.findViewById(R.id.tvRowDesc);
            tvRowDesc.setText(desc);

            tblMediaMetadata.addView(vRow);
        }

        Button btnEditMetadata = (Button) findViewById(R.id.btnEditMetadata);
        btnEditMetadata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsIntent = new Intent(mContext, ArchiveSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        Button btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent uploadIntent = new Intent(mContext, MainActivity.class);
                MainActivity.SHOULD_SPIN = true; // FIXME we cannot rely on statics to do inter activity communication
                startActivity(uploadIntent);
            }
        });
    }

    private boolean[] getMetadataPermissions() {
        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        boolean[] arPermissions = new boolean[6];

        //TODO pull this from DB instead of prefs - but for now **ORDER DOES MATTER***
        arPermissions[0] = sharedPref.getBoolean(Globals.INTENT_EXTRA_USE_TOR, false);
        arPermissions[1] = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TITLE, true);
        arPermissions[2] = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, false);
        arPermissions[3] = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_AUTHOR, false);
        arPermissions[4] = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_LOCATION, false);
        arPermissions[5] = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TAGS, false);

        return arPermissions;
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }
}