package io.scal.openarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;

import java.util.Date;
import java.util.List;

import io.scal.openarchive.db.Media;
import io.scal.openarchive.db.MediaMetadataSTV;
import io.scal.openarchive.db.Metadata;


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

        // declare needed vars
        final SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        final boolean isTitleShared = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TITLE, true);
        final boolean isDescriptionShared = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_DESCRIPTION, false);
        final boolean isAuthorShared = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_AUTHOR, false);
        final boolean isLocationShared = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_LOCATION, false);
        final boolean isTagsShared = sharedPref.getBoolean(Globals.INTENT_EXTRA_SHARE_TAGS, false);
        final boolean isTorUsed = sharedPref.getBoolean(Globals.INTENT_EXTRA_USE_TOR, false);

        TableRow trTitle = (TableRow) findViewById(R.id.tr_title);
        TableRow trDescription = (TableRow) findViewById(R.id.tr_description);
        TableRow trAuthor = (TableRow) findViewById(R.id.tr_author);
        TableRow trLocation = (TableRow) findViewById(R.id.tr_location);
        TableRow trTags = (TableRow) findViewById(R.id.tr_tags);
        TableRow trTor = (TableRow) findViewById(R.id.tr_tor);

        // show/hide data
        int visibility = isTitleShared ? View.VISIBLE : View.GONE;
        trTitle.setVisibility(visibility);

        visibility = isDescriptionShared ? View.VISIBLE : View.GONE;
        trDescription.setVisibility(visibility);

        visibility = isAuthorShared ? View.VISIBLE : View.GONE;
        trAuthor.setVisibility(visibility);

        visibility = isLocationShared ? View.VISIBLE : View.GONE;
        trLocation.setVisibility(visibility);

        visibility = isTagsShared ? View.VISIBLE : View.GONE;
        trTags.setVisibility(visibility);

        visibility = isTorUsed ? View.VISIBLE : View.GONE;
        trTor.setVisibility(visibility);

        // onclick listeners
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

                EditText etTitle = (EditText) findViewById(R.id.et_title);
                EditText etDescription = (EditText) findViewById(R.id.et_description);
                EditText etAuthor = (EditText) findViewById(R.id.et_author);
                EditText etLocation = (EditText) findViewById(R.id.et_location);
                EditText etTags = (EditText) findViewById(R.id.et_tags);

                // create new media
                Media media = new Media();
                media.setOriginalPath(mFilePath);
                media.setCreateDate(new Date());
                media.setUpdateDate(new Date());
                media.save();

                List<Metadata> metadataList = Metadata.listAll(Metadata.class);

                // save one record per metadata item
                MediaMetadataSTV mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(0), etTitle.getText().toString().trim(), isTitleShared);
                mediaMetadataSTV.save();

                mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(1), etDescription.getText().toString().trim(), isDescriptionShared);
                mediaMetadataSTV.save();

                mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(2), etAuthor.getText().toString().trim(), isAuthorShared);
                mediaMetadataSTV.save();

                mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(3), etLocation.getText().toString().trim(), isLocationShared);
                mediaMetadataSTV.save();

                mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(4), etTags.getText().toString().trim(), isTagsShared);
                mediaMetadataSTV.save();

                mediaMetadataSTV = new MediaMetadataSTV(media, metadataList.get(5), null, isTorUsed);
                mediaMetadataSTV.save();

                Intent uploadIntent = new Intent(mContext, MainActivity.class);
                uploadIntent.putExtra(Globals.INTENT_EXTRA_MEDIA_ID, media.getId());
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