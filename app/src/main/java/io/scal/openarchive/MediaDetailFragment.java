package io.scal.openarchive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import io.scal.openarchive.db.Media;

/**
 * A fragment representing a single Media detail screen.
 * This fragment is either contained in a {@link MediaListActivity}
 * in two-pane mode (on tablets) or a {@link MediaDetailActivity}
 * on handsets.
 */
public class MediaDetailFragment extends Fragment {
    private Media mMediaItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Globals.EXTRA_CURRENT_MEDIA_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String id = getArguments().getString(Globals.EXTRA_CURRENT_MEDIA_ID);
            mMediaItem = Media.getMediaById(Long.parseLong(id));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_media_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mMediaItem != null) {
            ((TextView) rootView.findViewById(R.id.media_detail)).setText(mMediaItem.getOriginalFilePath());
        }

        return rootView;
    }
}
