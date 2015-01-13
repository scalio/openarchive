package io.scal.openarchive.db;

import com.orm.SugarRecord;

/**
 * Created by micahjlucas on 1/11/15.
 */
public class Metadata extends SugarRecord<Metadata> {
    private String name;

    public Metadata() {};

    public Metadata(String name) {
        this.name = name;
    }


    /* getters and setters */
    public String getName() {
        return this.name;
    }
}