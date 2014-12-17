package io.scal.openarchive.database;

/**
 * Created by micahjlucas on 12/16/14.
 */
import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface MediaMetadataSTVTable {
    /** SQL type        Modifiers                   Reference Name            SQL Column Name */
    @DataType(INTEGER)  @NotNull                    String media_id         = "media_id";
    @DataType(INTEGER)  @NotNull                    String metadataId       = "metadata_id";
    @DataType(TEXT)                                 String value            = "value";
    @DataType(INTEGER)                              String shouldUpload     = "should_upload";
}
