package io.scal.openarchive;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class ViewMediaActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        init();
    }

    private void init() {
        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageDrawable(getResources().getDrawable(R.drawable.tunisia_sky));
    }
}
