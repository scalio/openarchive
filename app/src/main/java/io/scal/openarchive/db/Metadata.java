package io.scal.openarchive.db;

import com.orm.SugarRecord;

/**
 * Created by micahjlucas on 1/11/15.
 */
public class Metadata extends SugarRecord<Metadata> {
    String name;

    public Metadata(String name) {
        this.name = name;
    }
}