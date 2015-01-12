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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Util;

import java.util.ArrayList;


public class ReviewMediaActivity extends ActionBarActivity {
    private Context mContext = this;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        mFilePath = getIntent().getStringExtra(Constants.INTENT_EXTRA_FILE_PATH);
        init();
    }

    private void init() {
        if (mFilePath == null) {
            Utils.toastOnUiThread(this, "No Media Found!");
            finish();
        }

        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageURI(Uri.parse(mFilePath));

        TableRow trTitle = (TableRow) findViewById(R.id.tr_title);
        TableRow trDescription = (TableRow) findViewById(R.id.tr_description);
        TableRow trAuthor = (TableRow) findViewById(R.id.tr_author);
        TableRow trLocation = (TableRow) findViewById(R.id.tr_location);
        TableRow trTags = (TableRow) findViewById(R.id.tr_tags);
        TableRow trTor = (TableRow) findViewById(R.id.tr_tor);

        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        // show/hide data
        if (sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TITLE, true)) {
            trTitle.setVisibility(View.VISIBLE);
        } else {
            trTitle.setVisibility(View.GONE);
        }

        if(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, false)) {
            trDescription.setVisibility(View.VISIBLE);
        } else {
            trDescription.setVisibility(View.GONE);
        }

        if(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_AUTHOR, false)) {
            trAuthor.setVisibility(View.VISIBLE);
        } else {
            trAuthor.setVisibility(View.GONE);
        }

        if(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_LOCATION, false)) {
            trLocation.setVisibility(View.VISIBLE);
        } else {
            trLocation.setVisibility(View.GONE);
        }

        if(sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TAGS, false)) {
            trTags.setVisibility(View.VISIBLE);
        } else {
            trTags.setVisibility(View.GONE);
        }

        if(sharedPref.getBoolean(Globals.INTENT_EXTRA_USE_TOR, false)) {
            trTor.setVisibility(View.VISIBLE);
        } else {
            trTor.setVisibility(View.GONE);
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

    @Override
    protected void onPause() {
        super.onPause();

        // store file path
        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Globals.INTENT_CURRENT_MEDIA_PATH, mFilePath);
        editor.apply();

        // reset variable after storage
        mFilePath = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get file path if null
        if (mFilePath == null) {
            final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);
            mFilePath = sharedPref.getString(Globals.INTENT_CURRENT_MEDIA_PATH, null);

            init();
        }
    }
}