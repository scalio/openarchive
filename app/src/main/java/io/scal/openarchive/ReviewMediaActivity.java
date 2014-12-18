package io.scal.openarchive;

import android.content.Context;
import android.content.Intent;
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

import io.scal.openarchive.database.MetadataTable;
import io.scal.openarchive.database.OpenArchiveContentProvider;


public class ReviewMediaActivity extends ActionBarActivity {
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        init();
    }

    private void init() {
        Uri uri = OpenArchiveContentProvider.Metadata.METADATA;
        Cursor result = this.getContentResolver().query(uri, new String[] { MetadataTable.id, MetadataTable.name }, null, null, null);

        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageDrawable(getResources().getDrawable(R.drawable.tunisia_sky));

        TableLayout tblMediaMetadata = (TableLayout) findViewById(R.id.tblMediaMetadata);

        //TODO
        String[] testDesc = {"Yes", "The picture", "This is a description of the media.", "Micah Lucas", "San Francisco, CA", "tag 1, tag 2, tag 3"};
        int i = 0;


        while (result.moveToNext()) {
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
                MainActivity.SHOULD_SPIN = true;
                startActivity(uploadIntent);
            }
        });
    }
}