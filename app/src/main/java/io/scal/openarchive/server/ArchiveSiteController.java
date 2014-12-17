package io.scal.openarchive.server;

import io.scal.openarchive.ArchiveSettingsActivity;
import io.scal.openarchive.Util;
import io.scal.openarchive.server.model.Account;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class ArchiveSiteController extends SiteController {
	public static final String SITE_NAME = "Internet Archive";
	public static final String SITE_KEY = "archive";
	private static final String TAG = "ArchiveSiteController";
    static {
        METADATA_REQUEST_CODE = 1022783271;
    }

	private static final String ARCHIVE_API_ENDPOINT = "http://s3.us.archive.org";
	public static final MediaType MEDIA_TYPE = MediaType.parse("");

	public ArchiveSiteController(Context context, Handler handler, String jobId) {
		super(context, handler, jobId);
	}

	@Override
	public void startAuthentication(Account account) {
		Intent intent = new Intent(mContext, ArchiveLoginActivity.class);
		intent.putExtra(SiteController.EXTRAS_KEY_CREDENTIALS, account.getCredentials());
		((Activity) mContext).startActivityForResult(intent, SiteController.CONTROLLER_REQUEST_CODE);
		// FIXME not a safe cast, context might be a service
	}

	@Override
	public void upload(Account account, HashMap<String, String> valueMap) {
		Log.d(TAG, "Upload file: Entering upload");
        
		String mediaPath = valueMap.get(VALUE_KEY_MEDIA_PATH);
        boolean useTor = (valueMap.get(VALUE_KEY_USE_TOR).equals("true")) ? true : false;
        String fileName = mediaPath.substring(mediaPath.lastIndexOf("/")+1, mediaPath.length()); 
        String licenseUrl = valueMap.get(VALUE_KEY_LICENSE_URL);
        
		// TODO this should make sure we arn't accidentally using one of archive.org's metadata fields by accident
        String title = valueMap.get(VALUE_KEY_TITLE);
        boolean shareTitle = (valueMap.get(ArchiveSettingsActivity.INTENT_EXTRA_SHARE_TITLE).equals("true")) ? true : false;
        String slug = valueMap.get(VALUE_KEY_SLUG);
		String tags = valueMap.get(VALUE_KEY_TAGS);
		//always want to include these two tags
		tags += "presssecure,storymaker";
        boolean shareTags = (valueMap.get(ArchiveSettingsActivity.INTENT_EXTRA_SHARE_TAGS).equals("true")) ? true : false;
		String author = valueMap.get(VALUE_KEY_AUTHOR);
        boolean shareAuthor = (valueMap.get(ArchiveSettingsActivity.INTENT_EXTRA_SHARE_AUTHOR).equals("true")) ? true : false;
		String profileUrl = valueMap.get(VALUE_KEY_PROFILE_URL);
		String locationName = valueMap.get(VALUE_KEY_LOCATION_NAME);
        boolean shareLocation = (valueMap.get(ArchiveSettingsActivity.INTENT_EXTRA_SHARE_LOCATION).equals("true")) ? true : false;
		String body = valueMap.get(VALUE_KEY_BODY);
        boolean shareDescription = (valueMap.get(ArchiveSettingsActivity.INTENT_EXTRA_SHARE_DESCRIPTION).equals("true")) ? true : false;
        
		File file = new File(mediaPath);
		if (!file.exists()) {
			jobFailed(4000473, "Internet Archive upload failed: invalid file");
			return;
		}
		
		String mediaType = Util.getMediaType(mediaPath);		

		OkHttpClient client = new OkHttpClient();

		if (super.torCheck(useTor, super.mContext)) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ORBOT_HOST, ORBOT_HTTP_PORT));
			client.setProxy(proxy);
		}

        // FIXME we are putting a random 4 char string in the bucket name for collision avoidance, we might want to do this differently?
		String urlPath = null;
		String url = null;
        if (shareTitle) {
            String randomString = new Util.RandomString(4).nextString();
            urlPath = slug + "-" + randomString;
            url = ARCHIVE_API_ENDPOINT  + "/" + urlPath + "/" + fileName;
        } else {
            urlPath = new Util.RandomString(16).nextString(); // FIXME need to use real GUIDs
            url = ARCHIVE_API_ENDPOINT  + "/" + urlPath + "/" + fileName;
        }
		Log.d(TAG, "uploading to url: " + url);

		Request.Builder builder = new Request.Builder()
				.url(url)
				.put(RequestBody.create(MEDIA_TYPE, file))
				.addHeader("Accept", "*/*")
                .addHeader("x-amz-auto-make-bucket", "1")
                .addHeader("x-archive-meta-collection", "storymaker")
//				.addHeader("x-archive-meta-sponsor", "Sponsor 998")
				.addHeader("x-archive-meta-language", "eng") // FIXME pull meta language from story
				.addHeader("authorization", "LOW " + account.getUserName() + ":" + account.getCredentials());

		if (mediaType != null) {
			builder.addHeader("x-archive-meta-mediatype", mediaType);
		}
		
		if(shareAuthor && author != null) {
			builder.addHeader("x-archive-meta-author", author);		
			if (profileUrl != null) {
				builder.addHeader("x-archive-meta-authorurl", profileUrl);
			}
		}
		
		if (shareLocation && locationName != null) {
			builder.addHeader("x-archive-meta-location", locationName);
		}
        
        if (shareTags && tags != null) {
            String keywords = tags.replace(',', ';').replaceAll(" ", "");
            builder.addHeader("x-archive-meta-subject", keywords);
        }
        
        if (shareDescription && body != null) {
            builder.addHeader("x-archive-meta-description", body);
        }
        
        if (shareTitle && title != null) {
            builder.addHeader("x-archive-meta-title", title);
        }
        
        if (licenseUrl != null) {
            builder.addHeader("x-archive-meta-licenseurl", licenseUrl);
        }
		
		Request request = builder.build();

		UploadFileTask uploadFileTask = new UploadFileTask(client, request);
		uploadFileTask.execute();
	}

	class UploadFileTask extends AsyncTask<String, String, String> {
		private OkHttpClient client;
		private Request request;
		private Response response;

		public UploadFileTask(OkHttpClient client, Request request) {
			this.client = client;
			this.request = request;
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "Begin Upload");

			try {
				response = client.newCall(request).execute();
                Log.d(TAG, "response: " + response + ", body: " + response.body().string());
				if (!response.isSuccessful()) {
					jobFailed(4000001, "Archive upload failed: Unexpected Response Code: " + "response: " + response + ", body: " + response.body().string());
				} else {	
				    jobSucceeded(response.request().urlString());
				}
			} catch (IOException e) {
				jobFailed(4000002, "Archive upload failed: IOException");
				try {
					Log.d(TAG, response.body().string());
				} catch (IOException e1) {
				    Log.d(TAG, "exception: " + e1.getLocalizedMessage() + ", stacktrace: " + e1.getStackTrace());
				}
			}

			return "-1";
		}
	}

    @Override
    public void startMetadataActivity(Intent intent) {
//        get the intent extras and launch the new intent with them
    }
}