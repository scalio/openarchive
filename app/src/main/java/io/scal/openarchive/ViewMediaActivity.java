package io.scal.openarchive;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RemoteControlClient;
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


public class ViewMediaActivity extends ActionBarActivity {
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        init();
    }

    private void init() {
        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageDrawable(getResources().getDrawable(R.drawable.tunisia_sky));

        TableLayout tblMediaMetadata = (TableLayout) findViewById(R.id.tblMediaMetadata);
        String[] metadata = Globals.metadataValues;

        for(int i = 0; i < metadata.length; i++) {
            View vRow = getLayoutInflater().inflate(R.layout.row_media_metadata, null);

            TextView tv = (TextView) vRow.findViewById(R.id.tvRow);
            tv.setText(metadata[i]);

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
