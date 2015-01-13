package io.scal.openarchive.db;

import com.orm.SugarRecord;

/**
 * Created by micahjlucas on 1/12/15.
 */
public class MediaMetadataSTV extends SugarRecord<MediaMetadataSTV> {
    private Media media;
    private Metadata metadata;
    private String metadataValue;
    private boolean didUpload;

    public MediaMetadataSTV() {};

    public MediaMetadataSTV(Media media, Metadata metadata, String metadataValue, boolean didUpload) {
        this.media = media;
        this.metadata = metadata;
        this.metadataValue = metadataValue;
        this.didUpload = didUpload;
    }


    /* getters and setters */
    public Media getMedia() {
        return this.media;
    }
    public void setMedia(Media media) {
        this.media = media;
    }

    public Metadata getMetadata() {
        return this.metadata;
    }
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getMetadataValue() {
        return this.metadataValue;
    }
    public void setMetadataValue(String metadataValue) {
        this.metadataValue = metadataValue;
    }

    public boolean getDidUpload() {
        return this.didUpload;
    }
    public void setDidUpload(boolean didUpload) {
        this.didUpload = didUpload;
    }
}