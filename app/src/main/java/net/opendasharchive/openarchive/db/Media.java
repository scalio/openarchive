package net.opendasharchive.openarchive.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.orm.SugarRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.opendasharchive.openarchive.R;
import net.opendasharchive.openarchive.Utility;

/**
 * Created by micahjlucas on 1/11/15.
 */
public class Media extends SugarRecord<Media> {
    private String originalFilePath;
    private String scrubbedFilePath;
    private MEDIA_TYPE mediaType;
    private String thumbnailFilePath;
    private Date createDate;
    private Date updateDate;
    private String serverUrl;

    private String title;
    private String description;
    private String author;
    private String location;
    private String tags;
    private boolean useTor;

    public static enum MEDIA_TYPE {
        AUDIO, IMAGE, VIDEO;
    }

    //left public ONLY for Sugar ORM
    public Media() {};

    public Media(Context context, String originalFilePath, MEDIA_TYPE mediaType) {
        this.originalFilePath = originalFilePath;
        this.mediaType = mediaType;
        this.createDate = new Date();
        this.updateDate = this.createDate;

        this.title = context.getString(R.string.default_title);
//        this.tags = context.getString(R.string.default_tags);

        this.save();
    }


    /* getters and setters */
    public String getOriginalFilePath() {
        return this.originalFilePath;
    }
    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getScrubbedFilePath() {
        return this.scrubbedFilePath;
    }
    public void setScrubbedFilePath(String scrubbedFilePath) {
        this.scrubbedFilePath = scrubbedFilePath;
    }

    public MEDIA_TYPE getMediaType() {
        return mediaType;
    }

    public void setMediaType(MEDIA_TYPE mediaType) {
        this.mediaType = mediaType;
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }
    public void setThumbnailFilePath(String thumbnailFilePath) {
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public Date getCreateDate() {
        return this.createDate;
    }
    public String getFormattedCreateDate() {
        String format = "MM-dd-yyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        return sdf.format(this.createDate);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        // repace spaces and commas with semicolons
        tags = tags.replace(' ', ';');
        tags = tags.replace(',', ';');
        this.tags = tags;
    }

    public boolean getUseTor() {
        return useTor;
    }

    public void setUseTor(boolean useTor) {
        this.useTor = useTor;
    }

    public Bitmap getThumbnail(Context context) { // TODO: disk cache, multiple sizes
        Bitmap thumbnail = null;

        if (thumbnailFilePath == null) {
            String path = originalFilePath;
            Uri uri = Uri.parse(path);
            String lastSegment = uri.getLastPathSegment();
            boolean isDocumentProviderUri = path.contains("content:/") && (lastSegment.contains(":"));

            if (this.mediaType == MEDIA_TYPE.AUDIO) {
                thumbnail = BitmapFactory.decodeResource(context.getResources(), R.drawable.audio_waveform);
            } else if (this.mediaType == MEDIA_TYPE.IMAGE) {
                if (isDocumentProviderUri) {
                    // path of form : content://com.android.providers.media.documents/document/video:183
                    // An Android Document Provider URI. Thumbnail already generated
                    // TODO Because we need Context we can't yet override this behavior at MediaFile#getThumbnail
                    long id = Long.parseLong(Uri.parse(path).getLastPathSegment().split(":")[1]);
                    thumbnail = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                } else {
                    if (path.contains("content:/")) {
                        path = Utility.getRealPathFromURI(context, uri);
                    }
                    File originalFile = new File(path);
                    String fileName = originalFile.getName();
                    String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
                    thumbnailFilePath = path.substring(0, path.lastIndexOf(File.separator) + 1) + tokens[0] + "_thumbnail.png";
                    File thumbnailFile = new File(thumbnailFilePath);
                    if (thumbnailFile.exists()) {
                        thumbnail = BitmapFactory.decodeFile(thumbnailFilePath);
                    } else {
                        Bitmap bitMap = BitmapFactory.decodeFile(path);

                        try {
                            FileOutputStream thumbnailStream = new FileOutputStream(thumbnailFile);
                            thumbnail = ThumbnailUtils.extractThumbnail(bitMap, 400, 300); // FIXME figure out the real aspect ratio and size needed
                            thumbnail.compress(Bitmap.CompressFormat.PNG, 75, thumbnailStream); // FIXME make compression level configurable
                            thumbnailStream.flush();
                            thumbnailStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (this.mediaType == MEDIA_TYPE.VIDEO) {
                // path of form : content://com.android.providers.media.documents/document/video:183
                if (isDocumentProviderUri) {
                    // An Android Document Provider URI. Thumbnail already generated

                    long id = 0;
                    id = Long.parseLong(lastSegment.split(":")[1]);
                    return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                } else {
                    // Regular old File path
                    try {
                        //Log.d(" *** TESTING *** ", "CREATING NEW THUMBNAIL FILE FOR " + path);

                        // FIXME should not be stored in the source location, but a cache dir in our app folder on the sd or internal cache if there is no SD
                        // FIXME need to check datestamp on original file to check if our thumbnail is up to date
                        // FIXME this should be run from a background thread as it does disk access
                        if (path.contains("content:/")) {
                            path = Utility.getRealPathFromURI(context, uri);
                        }
                        File originalFile = new File(path);
                        String fileName = originalFile.getName();
                        String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
                        thumbnailFilePath = path.substring(0, path.lastIndexOf(File.separator) + 1) + tokens[0] + "_thumbnail.png";
                        File thumbnailFile = new File(thumbnailFilePath);
                        if (thumbnailFile.exists()) {
                            thumbnail = BitmapFactory.decodeFile(thumbnailFilePath);
                        } else {
                            FileOutputStream thumbnailStream = new FileOutputStream(thumbnailFile);

                            thumbnail = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                            if (thumbnail != null) {
                                thumbnail.compress(Bitmap.CompressFormat.PNG, 75, thumbnailStream); // FIXME make compression level configurable
                                thumbnailStream.flush();
                                thumbnailStream.close();
                            }
                        }
                    } catch (IOException ioe) {
                        return null;
                    }

                }
            }  else {
                Log.e(this.getClass().getName(), "can't create thumbnail file for " + path + ", unsupported medium");
                thumbnail = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_thumbnail);
            }

            // save new thumbnail path
            this.save();
        } else {
            thumbnail = BitmapFactory.decodeFile(thumbnailFilePath);
        }

        // set to default if none found
        if(null == thumbnail) {
            thumbnail = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        }

        return thumbnail;
    }

    public static List<Media> getAllMediaAsList() {
        return Media.listAll(Media.class);
    }

    public static Media[] getAllMediaAsArray() {
        List mediaList = getAllMediaAsList();
        return (Media[]) mediaList.toArray(new Media[mediaList.size()]);
    }

    public static Media getMediaById(long mediaId) {
        return Media.findById(Media.class, mediaId);
    }

    public static void deleteMediaById(long mediaId) {
        Media media = Media.findById(Media.class, mediaId);
        media.delete();
    }
}