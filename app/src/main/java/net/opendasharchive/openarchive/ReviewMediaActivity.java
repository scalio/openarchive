package net.opendasharchive.openarchive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import net.opendasharchive.openarchive.db.Media;

import java.util.HashMap;

import io.scal.secureshareui.controller.SiteController;
import io.scal.secureshareui.model.Account;


public class ReviewMediaActivity extends ActionBarActivity {
    private static String TAG = "ReviewMediaActivity";

    private Context mContext = this;
    private Media mMedia;
    private ProgressDialog progressDialog = null;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        getMetadataValues();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

//        if (drawerListener.onOptionsItemSelected(item)) {
//            return true;
//        }

        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Intent intent = getIntent();

        // get intent extras
        long currentMediaId = intent.getLongExtra(Globals.EXTRA_CURRENT_MEDIA_ID, -1);

        // get default metadata sharing values
        SharedPreferences sharedPref = this.getSharedPreferences(Globals.PREF_FILE_KEY, Context.MODE_PRIVATE);

        isTitleShared = sharedPref.getBoolean(Globals.PREF_SHARE_TITLE, true);
        isDescriptionShared = sharedPref.getBoolean(Globals.PREF_SHARE_DESCRIPTION, false);
        isAuthorShared = sharedPref.getBoolean(Globals.PREF_SHARE_AUTHOR, false);
        isLocationShared = sharedPref.getBoolean(Globals.PREF_SHARE_LOCATION, false);
        isTagsShared = sharedPref.getBoolean(Globals.PREF_SHARE_TAGS, false);
        isTorUsed = sharedPref.getBoolean(Globals.PREF_USE_TOR, false);

        // check for new file or existing media
        if (currentMediaId >= 0) {
            mMedia = Media.findById(Media.class, currentMediaId);
        } else {
            Utility.toastOnUiThread(this, getString(R.string.error_no_media));
            finish();
            return;
        }

        // display media preview if available
        ImageView ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageBitmap(mMedia.getThumbnail(mContext));

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
                Context context = ReviewMediaActivity.this;
                SiteController siteController = SiteController.getSiteController("archive", context, mHandler, null);

                Account account = new Account(context, null);


                HashMap<String, String> valueMap = ArchiveSettingsActivity.getMediaMetadata(ReviewMediaActivity.this, mMedia);

                siteController.upload(account, valueMap);
                showProgressSpinner();
//                Intent uploadIntent = new Intent(mContext, MainActivity.class);
//                uploadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                uploadIntent.putExtra(Globals.EXTRA_CURRENT_MEDIA_ID, mMedia.getId());
//                MainActivity.SHOULD_SPIN = true; // FIXME we cannot rely on statics to do inter activity communication
//                startActivity(uploadIntent);
            }
        });
    }

    private void closeProgressSpinner() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void showProgressSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading_title));
        progressDialog.setMessage(getString(R.string.loading_message));
        progressDialog.show();

//        Thread progressThread = new Thread(){
//            @Override
//            public void run(){
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    progressDialog.dismiss();
//                }
//            }
//        };
//        progressThread.start();
    }

    private void getMetadataValues() {
        if(null == mMedia) {
            return;
        }

        // set default values
        final TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        final TextView tvDescription = (TextView) findViewById(R.id.tv_description);
        final TextView tvAuthor = (TextView) findViewById(R.id.tv_author);
        final TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        final TextView tvTags = (TextView) findViewById(R.id.tv_tags);

        tvTitle.setText(mMedia.getTitle());
        tvDescription.setText(mMedia.getDescription());
        tvAuthor.setText(mMedia.getAuthor());
        tvLocation.setText(mMedia.getLocation());
        tvTags.setText(mMedia.getTags());
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        getMetadataValues();
    }

    static HandlerThread bgThread = new HandlerThread("VideoRenderHandlerThread");
    static {
        bgThread.start();
    }
    public Handler mHandler = new Handler(bgThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();

            String jobIdString = data.getString(SiteController.MESSAGE_KEY_JOB_ID);
            int jobId = (jobIdString != null) ? Integer.parseInt(jobIdString) : -1;

            int messageType = data.getInt(SiteController.MESSAGE_KEY_TYPE);
            switch (messageType) {
                case SiteController.MESSAGE_TYPE_SUCCESS:
                    String result = data.getString(SiteController.MESSAGE_KEY_RESULT);
                    String resultUrl = getDetailsUrlFromResult(result);
                    mMedia.setServerUrl(resultUrl);
                    mMedia.save();
                    showPublished(resultUrl);
                    closeProgressSpinner();
                    break;
                case SiteController.MESSAGE_TYPE_FAILURE:
                    int errorCode = data.getInt(SiteController.MESSAGE_KEY_CODE);
                    String errorMessage = data.getString(SiteController.MESSAGE_KEY_MESSAGE);
                    String error = "Error " + errorCode + ": " + errorMessage;
                    closeProgressSpinner();
                    showError(error);
                    Log.d(TAG, "upload error: " + error);
                    break;
                case SiteController.MESSAGE_TYPE_PROGRESS:
                    String message = data.getString(SiteController.MESSAGE_KEY_MESSAGE);
                    float progress = data.getFloat(SiteController.MESSAGE_KEY_PROGRESS);
                    Log.d(TAG, "upload progress: " + progress);
                    // TODO implement a progress dialog to show this
                    break;
            }
        }
    };

    // result is formatted like http://s3.us.archive.org/Default-Title-19db/JPEG_20150123_160341_-1724212344_thumbnail.png
    public String getDetailsUrlFromResult(String result) {
//        String slug = ArchiveSettingsActivity.getSlug(mMedia.getTitle());
        String[] splits = result.split("/");
        String slug = splits[3];

        return "http://archive.org/details/" + slug;
    }

    public void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(!isFinishing()){
                    new AlertDialog.Builder(ReviewMediaActivity.this)
                            .setTitle("Upload Error")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReviewMediaActivity.this.finish();
                                }
                            }).create().show();
                }
            }
        });
    }

    public void showPublished(final String postUrl) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "dialog for showing published url: " + postUrl);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(postUrl));
                                startActivity(i);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewMediaActivity.this);
                builder.setMessage(getString(R.string.view_published_media_online))
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });
    }
}