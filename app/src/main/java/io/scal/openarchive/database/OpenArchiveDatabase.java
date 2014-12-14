package io.scal.openarchive.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

import io.scal.openarchive.Constants;

/**
 * Created by micahjlucas on 12/12/14.
 */
@Database(version = Constants.DATABASE_VERSION)
public class OpenArchiveDatabase {

    /** Table Definition        Reference Name                        SQL Tablename */
    @Table(UserTable.class)      public static final String USER        = "user";
    @Table(MediaTable.class)     public static final String MEDIA       = "media";
}