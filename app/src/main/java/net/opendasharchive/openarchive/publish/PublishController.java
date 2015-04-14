package net.opendasharchive.openarchive.publish;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import net.opendasharchive.openarchive.publish.model.Job;
import net.opendasharchive.openarchive.publish.model.JobTable;
import net.opendasharchive.openarchive.publish.model.PublishJob;
import net.opendasharchive.openarchive.publish.model.PublishJobTable;
import net.opendasharchive.openarchive.publish.sites.ArchivePublisher;
import io.scal.secureshareui.controller.ArchiveSiteController;

// TODO we need to make sure this will be thread safe since upload and render jobs are on separate threads and could callback in a race here

/**
 * 
 * @author Josh Steiner <josh@vitriolix.com>
 *
 */
public class PublishController {
    private final String TAG = "PublishController";
    
	private static PublishController publishController = null;
	private Context mContext;
	UploadWorker uploadWorker;
	PublisherBase publisher = null;
	PublishJob mPublishJob = null;
	PublishListener mListener;
	
	public PublishController(Context context, PublishListener listener) {
	    mContext = context;
	    mListener = listener;
	}

	public static PublishController getInstance(Activity activity, PublishListener listener) {
		if (publishController == null) {
			publishController = new PublishController(activity, listener);
		}
		
		return publishController;
	}
	
    // FIXME this won't help us get more than one publisher per run
    public PublisherBase getPublisher(PublishJob publishJob) {
        String[] keys = publishJob.getSiteKeys();
        if (keys == null) {
            return null;
        } else {
            List<String> ks = Arrays.asList(keys);
            if (ks.contains(ArchiveSiteController.SITE_KEY)) {
                publisher = new ArchivePublisher(mContext, this, publishJob);
            }
        }

        return publisher;
    }
    
    // FIXME this won't help us get more than one publisher per run
    public static Class getPublisherClass(String site) {
        if (site.equals(ArchiveSiteController.SITE_KEY)) {
            return ArchiveSiteController.class;
        }

        return null;
    }

    public void startUpload(int publishJobId) {
        PublishJob publishJob = getPublishJob(publishJobId);
        PublisherBase publisher = getPublisher(publishJob);
        // TODO this needs to loop a few times until publisher start returns false or something to tell us that the publish job is totally finished
        if (publisher != null) {
            publisher.startUpload();
        }
    }
    
    // FIXME we are sort of half caching here, either cache or don't and get rid of the mPublishJob, it's confusing
    private PublishJob getPublishJob(int publishJobId) {
    	mPublishJob = (PublishJob) (new PublishJobTable()).get(mContext, publishJobId);
    	return mPublishJob;
    }
    
    // FIXME url here should be grabbedfrom the publishJob, not passed
	public void publishJobSucceeded(PublishJob publishJob, String url) {
	    // get an embeddable publish
	    
		mListener.publishSucceeded(publishJob, url);
	}
    
    public void publishJobFailed(PublishJob publishJob, int errorCode, String errorMessage) {
        mListener.publishFailed(publishJob, errorCode, errorMessage);
    }
	
    /**
     * Aggregates and filters progress from each job associated with a publish job
     *
     * @param progress 0 to 1
     * @param message message displayed to the user
     */
	public void publishJobProgress(PublishJob publishJob, float progress, String message) {
	    mListener.publishProgress(publishJob, progress, message);
	}
	
	public void jobSucceeded(Job job, String code) {
        Log.d(TAG, "jobSucceeded: " + job + ", with code: " + code);
		// TODO need to raise this to the interested activities here
        PublishJob publishJob = job.getPublishJob();
        PublisherBase publisher = getPublisher(publishJob);
        if (publisher != null) {
            publisher.jobSucceeded(job);
        } else {
            // TODO how to handle null publisher?
        }
        mListener.jobSucceeded(job);
	}
	
	public void jobFailed(Job job, int errorCode, String errorMessage) {
        Log.d(TAG, "jobFailed: " + job + ", with errorCode: " + errorCode + ", and errorMessage: " + errorMessage);
		// TODO need to raise this to the interested activities here
        PublishJob publishJob = job.getPublishJob();
        PublisherBase publisher = getPublisher(publishJob);
        if (publisher != null) {
            publisher.jobFailed(job, errorCode, errorMessage);
        } else {
            // TODO how to handle null publisher?
        }
        mListener.jobFailed(job, errorCode, errorMessage);
	}


    /**
     * 
     * @param job
     * @param progress 0 to 1
     * @param message Message displayed to user
     */
    public void jobProgress(Job job, float progress, String message) {
        PublishJob publishJob = job.getPublishJob();
        PublisherBase publisher = getPublisher(publishJob);
        if (publisher != null) {
            publisher.jobProgress(job, progress, message);
        } else {
            // TODO how to handle null publisher?
        }
    }
	
	private void startUploadService() {
        uploadWorker = UploadWorker.getInstance(mContext, this);
        uploadWorker.start(mPublishJob);
    }
	
	public void enqueueJob(Job job) {
		job.setQueuedAtNow();
		job.save();
		if (job.isType(JobTable.TYPE_UPLOAD)) {
			startUploadService();
		}
	}
	
	public static interface PublishListener {
        public void publishSucceeded(PublishJob publishJob, String url);

        public void publishFailed(PublishJob publishJob, int errorCode, String errorMessage);
        
        public void jobSucceeded(Job job);

        public void jobFailed(Job job, int errorCode, String errorMessage);
        
        public void publishProgress(PublishJob publishJob, float progress, String message);
	}

}
