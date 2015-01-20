package io.scal.openarchive;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;


public class MediaListActivity extends FragmentActivity
        implements MediaListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(String id) {
        Intent reviewMediaIntent = new Intent(this, ReviewMediaActivity.class);
        reviewMediaIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, Long.parseLong(id));
        startActivity(reviewMediaIntent);
    }
}
