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
        while (result.moveToNext()) {
            String s = result.getString(0);
            String s2 = result.getString(1);

            View vRow = getLayoutInflater().inflate(R.layout.row_media_metadata, null);

            TextView tv = (TextView) vRow.findViewById(R.id.tvRow);
            tv.setText(s + s2);

            CheckBox cb = (CheckBox) vRow.findViewById(R.id.cbRow);
            cb.setChecked(false);

            tblMediaMetadata.addView(vRow);
        }

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsIntent = new Intent(mContext, MainActivity.class);
                MainActivity.SHOULD_SPIN = true;
                startActivity(settingsIntent);
            }
        });
    }
}
