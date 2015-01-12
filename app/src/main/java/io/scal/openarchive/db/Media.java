package io.scal.openarchive.db;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by micahjlucas on 1/11/15.
 */
public class Media extends SugarRecord<Media> {
    int id;
    String originalPath;
    String scrubbedPath;
    Date createDate;
    Date updateDate;
    String serverUrl;
}