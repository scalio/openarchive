package io.scal.openarchive.db;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by micahjlucas on 1/11/15.
 */
public class Media extends SugarRecord<Media> {
    private String originalPath;
    private String scrubbedPath;
    private Date createDate;
    private Date updateDate;
    private String serverUrl;

    public Media() {};

    public Media(String originalPath, String scrubbedPath, Date createDate, Date updateDate, String serverUrl) {
        this.originalPath = originalPath;
        this.scrubbedPath = scrubbedPath;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.serverUrl = serverUrl;
    }


    /* getters and setters */
    public String getOriginalPath() {
        return this.originalPath;
    }
    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getScrubbedPath() {
        return this.scrubbedPath;
    }
    public void setScrubbedPath(String scrubbedPath) {
        this.scrubbedPath = scrubbedPath;
    }

    public Date getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}