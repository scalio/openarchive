package io.scal.openarchive.database;

/**
 * Created by micahjlucas on 12/12/14.
 */

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.BLOB;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface MediaTable {

    /** SQL type        Modifiers                   Reference Name            SQL Column Name */
    @DataType(INTEGER)  @PrimaryKey @AutoIncrement  String id               = "_id";
    @DataType(INTEGER)  @NotNull                    String userId           = "user_id";
    @DataType(BLOB)     @NotNull                    String mediaBlob        = "media_blob";
    @DataType(TEXT)                                 String originalPath     = "original_path";
    @DataType(TEXT)                                 String scrubbedPath     = "scrubbed_path";
    @DataType(INTEGER)  @NotNull                    String createdDate      = "create_date";
    @DataType(INTEGER)  @NotNull                    String updatedDate      = "updated_date";
    @DataType(TEXT)                                 String serverURL        = "server_url";
}
