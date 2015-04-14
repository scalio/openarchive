package net.opendasharchive.openarchive;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import net.opendasharchive.openarchive.db.Media;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.opendasharchive  .openarchive.R;


public class FragmentMain extends Fragment {
    private static final String TAG = "MainFragment";

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    public static FragmentMain newInstance() {
        FragmentMain fragment = new FragmentMain();
        return fragment;
    }

    public FragmentMain() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        init(view);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private void init(final View view) {
        Button btnImport = (Button) view.findViewById(R.id.btnImport);
        Button btnCapture = (Button) view.findViewById(R.id.btnCapture);

        btnImport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ACTION_OPEN_DOCUMENT is the new API 19 action for the Android file manager
                Intent intent;
                int requestId = Globals.REQUEST_FILE_IMPORT;
                if (Build.VERSION.SDK_INT >= 19) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                //String cardMediaId = mCardModel.getStoryPath().getId() + "::" + mCardModel.getId() + "::" + MEDIA_PATH_KEY;
                // Apply is async and fine for UI thread. commit() is synchronous
                //mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString(Constants.PREFS_CALLING_CARD_ID, cardMediaId).apply();
                ((Activity) mContext).startActivityForResult(intent, requestId);
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = null;
                int requestId = -1;
                Spinner spCaptureOptions = (Spinner) view.findViewById(R.id.spCaptureOptions);
                Media.MEDIA_TYPE mediaType = getSelectedMediaType(spCaptureOptions.getSelectedItemPosition());

                if (mediaType == Media.MEDIA_TYPE.AUDIO) {
                    intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    requestId = Globals.REQUEST_AUDIO_CAPTURE;

                } else if (mediaType == Media.MEDIA_TYPE.IMAGE) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e(TAG, "Unable to make image file");
                        return;
                    }

                    mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putString(Globals.EXTRA_FILE_LOCATION, photoFile.getAbsolutePath()).apply();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    requestId = Globals.REQUEST_IMAGE_CAPTURE;

                } else if (mediaType == Media.MEDIA_TYPE.VIDEO) {
                    intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    requestId = Globals.REQUEST_VIDEO_CAPTURE;
                }

                if (null != intent && intent.resolveActivity(mContext.getPackageManager()) != null) {
                    ((Activity) mContext).startActivityForResult(intent, requestId);
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private Media.MEDIA_TYPE getSelectedMediaType(int pos) {
        // set image as default TODO set to null and display error
        Media.MEDIA_TYPE mediaType = Media.MEDIA_TYPE.IMAGE;

        switch (pos) {
            case 0:
                mediaType = Media.MEDIA_TYPE.IMAGE;
                break;
            case 1:
                mediaType = Media.MEDIA_TYPE.VIDEO;
                break;
            case 2:
                mediaType = Media.MEDIA_TYPE.AUDIO;
                break;
        }

        return mediaType;
    }
}