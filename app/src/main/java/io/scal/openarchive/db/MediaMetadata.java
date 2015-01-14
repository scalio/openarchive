package io.scal.openarchive.db;

import com.orm.SugarRecord;

/**
 * Created by micahjlucas on 1/12/15.
 */
public class MediaMetadata extends SugarRecord<MediaMetadata> {
    private Media media;
    private Metadata metadata;
    private String value;
    private boolean isShared;

    public MediaMetadata() {};

    public MediaMetadata(Media media, Metadata metadata, String metadataValue, boolean isShared) {
        this.media = media;
        this.metadata = metadata;
        this.value = metadataValue;
        this.isShared = isShared;
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

    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
        this.save();
    }

    public boolean getIsShared() { return this.isShared; }
    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }
}