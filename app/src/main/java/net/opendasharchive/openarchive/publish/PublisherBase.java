package net.opendasharchive.openarchive.publish;

import android.content.Context;
import android.util.Log;

import net.opendasharchive.openarchive.publish.model.Job;
import net.opendasharchive.openarchive.publish.model.JobTable;
import net.opendasharchive.openarchive.publish.model.PublishJob;

public abstract class PublisherBase {
    private final static String TAG = "PublisherBase";
    protected PublishController mController;
    protected PublishJob mPublishJob;
    protected Context mContext;
    
    public PublisherBase(Context context, PublishController publishController, PublishJob publishJob) {
        mContext = context;
        mController = publishController;
        mPublishJob = publishJob;
    }

    
    public abstract void startUpload();
	
//	public abstract void jobSucceeded(Job job);

    public abstract String getEmbed(Job job);

    public abstract String getResultUrl(Job job);
	
	private Job getPreferredUploadJob() {
	    // TODO this should chose from all the selected sites for this publish the preferred site from this list: youtube, facebook, ...
	    for (Job job: mPublishJob.getJobsAsList()) {
	        if (job.isType(JobTable.TYPE_UPLOAD)) {
				//job.setSpec(getMedium()); TODO
				return job;
	        }
	    }
	    return null;
	}

    public void publishSucceeded(String url) {
//        mPublishJob.setResult(url); // FIXME implement this
        mPublishJob.setFinishedAtNow();
        mPublishJob.save();
        mController.publishJobSucceeded(mPublishJob, url);
    }

    public void publishFailed(int errorCode, String errorMessage) {
        mController.publishJobFailed(mPublishJob, errorCode, errorMessage);
    }
    
    public void jobSucceeded(Job job) {
        Log.d(TAG, "jobSucceeded: " + job);
        if (job.isType(JobTable.TYPE_UPLOAD)) {
            publishSucceeded(getResultUrl(job));
		}
	}
    
    public void jobFailed(Job job, int errorCode, String errorMessage) {
        Log.d(TAG, "jobFailed: " + job);
        mController.publishJobFailed(mPublishJob, errorCode, errorMessage);
    }

    /**
     * 
     * @param job
     * @param progress 0 to 1
     * @param message Message displayed to user
     */
    public void jobProgress(Job job, float progress, String message) {
        mController.publishJobProgress(mPublishJob, progress, message);
    }
}
