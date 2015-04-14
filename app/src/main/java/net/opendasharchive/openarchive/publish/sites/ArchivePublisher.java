package net.opendasharchive.openarchive.publish.sites;

import android.content.Context;
import android.util.Log;

import net.opendasharchive.openarchive.publish.PublishController;
import net.opendasharchive.openarchive.publish.model.Job;
import net.opendasharchive.openarchive.publish.model.JobTable;
import net.opendasharchive.openarchive.publish.model.PublishJob;

import java.util.Locale;

import net.opendasharchive.openarchive.Globals;
import net.opendasharchive.openarchive.publish.PublisherBase;

public class ArchivePublisher extends PublisherBase {
	private final String TAG = "ArchivePublisher";

	private static final String ARCHIVE_URL_DOWNLOAD = "https://archive.org/download/";
	private static final String ARCHIVE_API_ENDPOINT = "http://s3.us.archive.org/";

	public ArchivePublisher(Context context, PublishController publishController, PublishJob publishJob) {
		super(context, publishController, publishJob);
	}

	public void startUpload() {
		Log.d(TAG, "startUpload");
		Job newJob = new Job(mContext, mPublishJob.getId(), JobTable.TYPE_UPLOAD, Globals.SITE_ARCHIVE,null);
		mController.enqueueJob(newJob);
	}

    @Override
	public String getEmbed(Job job) {
		if(null == job) {
			return null;
		}

		String medium = job.getSpec();
		String fileURL = job.getResult();
		String width = null;
		String height = null;
		String cleanFileURL = null;

        //FIXME need to add Media.MEDIA_TYPE check instead of strings
        /*
		if (medium != null) {
			if (medium.equals(Globals.PHOTO)) {
				// keep default image size
				width = "";
				height = "";
			} else if (medium.equals(Globals.VIDEO)) {
				width = "600";
				height = "480";
			} else if (medium.equals(Globals.AUDIO)) {
				width = "500";
				height = "30";
			}

			cleanFileURL = cleanFileURL(fileURL);
		}*/

		String embed  = null;
		if (null != width && null != height && null != cleanFileURL) {
			embed = String.format(Locale.US, "[archive %s %s %s]", cleanFileURL, width, height);

			/*
			if(isMediumPhoto) {
				embed = String.format(Locale.US, "<img src='%s' alt='Archive Embed'>" ,
													ARCHIVE_URL_DOWNLOAD + cleanFileURL);
			} else {
				embed = String.format(Locale.US, "<iframe " +
												"src='%s' " +
												"width='%s' " +
												"height='%s' " +
												"frameborder='0' " +
												"webkitallowfullscreen='true' " +
												"mozallowfullscreen='true' allowfullscreen>" +
												"</iframe>",
												ARCHIVE_URL_DOWNLOAD + cleanFileURL, width, height);
			}
			*/
		}

		return embed;
	}


    @Override
    public String getResultUrl(Job job) {
        return null; // FIXME implement getResultUrl
    }

    private String cleanFileURL(String fileURL) {
		fileURL = fileURL.replace(ARCHIVE_API_ENDPOINT, "");
		return fileURL;
	}
}
