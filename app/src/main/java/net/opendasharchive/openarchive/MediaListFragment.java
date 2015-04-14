package net.opendasharchive.openarchive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.opendasharchive.openarchive.db.Media;

public class MediaListFragment extends ListFragment {

    public MediaAdapter mMediaAdapter;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MediaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMediaAdapter();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        // Init onClickListeners
        getListView().setOnItemClickListener(onItemClickListener);
        getListView().setOnItemLongClickListener(onItemLongClickListener);
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent reviewMediaIntent = new Intent(getActivity(), ReviewMediaActivity.class);
            reviewMediaIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, getMediaIdByPosition(position));
            startActivity(reviewMediaIntent);
        }
    };

    ListView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.alert_lbl_delete_media)
                    .setCancelable(true)
                    .setMessage(R.string.alert_delete_media)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Media.deleteMediaById(getMediaIdByPosition(position));
                            initMediaAdapter();
                            mMediaAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), R.string.alert_media_deleted, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(R.drawable.ic_dialog_alert_holo_light)
                    .show();

            return true;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private void initMediaAdapter() {
        mMediaAdapter = new MediaAdapter(this.getActivity(), R.layout.activity_media_list_row, Media.getAllMediaAsList());
        setListAdapter(mMediaAdapter);
    }

    private long getMediaIdByPosition(int position) {
        return Media.getAllMediaAsList().get(position).getId();
    }
}