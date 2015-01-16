package io.scal.openarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import io.scal.openarchive.db.Media;
import io.scal.openarchive.db.MediaMetadata;


public class ReviewMediaActivity extends ActionBarActivity {
    private Context mContext = this;
    private Media mMedia;

    boolean isTitleShared;
    boolean isDescriptionShared;
    boolean isAuthorShared;
    boolean isLocationShared;
    boolean isTagsShared;
    boolean isTorUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_media);

        init();
        getMetadataValues();
    }

    private void init() {
        Intent intent = getIntent();

        // get intent extras
        long currentMediaId = intent.getLongExtra(Globals.EXTRA_CURRENT_MEDIA_ID, 0);

        // get default metadata sharing values
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        isTitleShared = sharedPref.getBoolean(Globals.PREF_SHARE_TITLE, true);
        isDescriptionShared = sharedPref.getBoolean(Globals.PREF_SHARE_DESCRIPTION, false);
        isAuthorShared = sharedPref.getBoolean(Globals.PREF_SHARE_AUTHOR, false);
        isLocationShared = sharedPref.getBoolean(Globals.PREF_SHARE_LOCATION, false);
        isTagsShared = sharedPref.getBoolean(Globals.PREF_SHARE_TAGS, false);
        isTorUsed = sharedPref.getBoolean(Globals.PREF_USE_TOR, false);

        // check for new file or existing media
        if (currentMediaId != 0) {
            mMedia = Media.findById(Media.class, currentMediaId);
        } else {
            Utility.toastOnUiThread(this, getString(R.string.error_no_media));
            finish();
        }

        // display media preview
        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageBitmap(mMedia.getThumbnail(getApplicationContext()));

        // show/hide data rows
        TableRow trTitle = (TableRow) findViewById(R.id.tr_title);
        TableRow trDescription = (TableRow) findViewById(R.id.tr_description);
        TableRow trAuthor = (TableRow) findViewById(R.id.tr_author);
        TableRow trLocation = (TableRow) findViewById(R.id.tr_location);
        TableRow trTags = (TableRow) findViewById(R.id.tr_tags);
        TableRow trTor = (TableRow) findViewById(R.id.tr_tor);

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
                settingsIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, mMedia.getId());
                startActivity(settingsIntent);
            }
        });

        Button btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent uploadIntent = new Intent(mContext, MainActivity.class);
                uploadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                uploadIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, mMedia.getId());
                MainActivity.SHOULD_SPIN = true; // FIXME we cannot rely on statics to do inter activity communication
                startActivity(uploadIntent);
            }
        });
    }

    private void getMetadataValues() {
        // set default values
        final TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        final TextView tvDescription = (TextView) findViewById(R.id.tv_description);
        final TextView tvAuthor = (TextView) findViewById(R.id.tv_author);
        final TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        final TextView tvTags = (TextView) findViewById(R.id.tv_tags);

        List<MediaMetadata> mediaMetadataList = MediaMetadata.find(MediaMetadata.class, "media = ?", new String[]{mMedia.getId().toString()});

        // iterate over metadata and retrieve values
        TextView tvCurrent = null;
        for (MediaMetadata mm : mediaMetadataList) {
            long metadataId = mm.getMetadata().getId();

            if (metadataId == 1) {
                tvCurrent = tvTitle;
            }
            else if (metadataId == 2) {
                tvCurrent = tvDescription;
            }
            else if (metadataId == 3) {
                tvCurrent = tvAuthor;
            }
            else if (metadataId == 4) {
                tvCurrent = tvLocation;
            }
            else if (metadataId == 5) {
                tvCurrent = tvTags;
            }
            tvCurrent.setText(mm.getValue());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        getMetadataValues();
    }
}