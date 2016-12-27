package taggedit.com.teggedit.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import taggedit.com.teggedit.model.TagPhoto;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/23/17.
 */

public class PhotoTagsCurdHelper {

    public static long insertOrUpdatePhotTag(TagPhoto tagPhoto, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH, tagPhoto.getPhotoPath());
        contentValues.put(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_IDS, tagPhoto.getPhotoTagIds());
        contentValues.put(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES, tagPhoto.getPhotoTagsName());
        Uri uri = PhotoTagsContract.PhotoTagEntry.CONTENT_URI;
        Uri resultUri = context.getContentResolver().insert(uri, contentValues);

        if (resultUri == null) {
            uri.buildUpon().appendPath(tagPhoto.getAutoIncrementId() + "");
            int count = context.getContentResolver().update(uri, contentValues, PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_ID + " = " + tagPhoto.getAutoIncrementId(), null);
            if (count != 0) {
                MyLogger.d("PhotoTagsCurdHelper", "updated item count is :: " + count);
                return tagPhoto.getAutoIncrementId();
            } else {
                return -1;
            }

        } else {
            MyLogger.d("PhotoTagsCurdHelper", "inserted item uri is :: " + resultUri);
            return ContentUris.parseId(resultUri);
        }
    }
}
