package taggedit.com.teggedit.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import taggedit.com.teggedit.model.Tag;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/22/17.
 */

public class TagsCurdHelper {

    public static long insertTag(Tag tag, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME, tag.getName());
        Uri uri = PhotoTagsContract.TagsEntry.CONTENT_URI;
        Uri resultUri = context.getContentResolver().insert(uri, contentValues);

        if (resultUri == null) {
            return -1;
        } else {
            return ContentUris.parseId(resultUri);
        }
    }

    public static ArrayList<Tag> getTagsFromIds(String tagIds, Context context) {
        if (tagIds != null && !tagIds.equals("")) {
            String ids[] = tagIds.split(",");
            Cursor cursorTag = context.getContentResolver().query(PhotoTagsContract.TagsEntry.CONTENT_URI, null, PhotoTagsContract.TagsEntry.COLUMN_TAG_ID + " IN (" + tagIds + ")", null, null);
            if (cursorTag != null && cursorTag.getCount() > 0) {
                ArrayList<Tag> databaseTags = new ArrayList<>(1);
                while (cursorTag.moveToNext()) {
                    Tag tag = new Tag(cursorTag.getInt(cursorTag.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_ID)), cursorTag.getString(cursorTag.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME)));
                    databaseTags.add(tag);
                }
                cursorTag.close();
                return databaseTags;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void deleteTag(Tag tag, Context context) {
        int count = context.getContentResolver().delete(PhotoTagsContract.TagsEntry.CONTENT_URI, PhotoTagsContract.TagsEntry.COLUMN_TAG_ID + " = " + tag.getId(), null);
        MyLogger.d("deleteTag", "deleted successfully " + count);
    }

    private String[] getTagIdArray(String val) {
        if (val != null && !val.equals("")) {
            return val.split(",");
        } else {
            return null;
        }
    }

//    public static ArrayList<Tag> getTagName(String tagName, Context context) {
//        if (tagName != null && !tagName.equals("")) {
//            Cursor cursorTag = context.getContentResolver().query(PhotoTagsContract.PhotoTagEntry.CONTENT_URI, null, PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES + " LIKE \'*" + tagName + "*\'", null, null);
//            if (cursorTag != null && cursorTag.getCount() > 0) {
//                ArrayList<TagPhoto> databasePhotoTags = new ArrayList<>(1);
//                while (cursorTag.moveToNext()) {
//                    TagPhoto tagPhoto = new TagPhoto();
//                    tagPhoto.setPhotoTagIds(cursorTag.getString(cursorTag.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_IDS)));
//                    tagPhoto.setPhotoPath(cursorTag.getString(cursorTag.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH)));
//                    tagPhoto.setAutoIncrementId(cursorTag.getLong(cursorTag.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_ID)));
//                    tagPhoto.setPhotoTagsName(cursorTag.getString(cursorTag.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES)));
//                    MyLogger.d("PhotoTagCurdHelper", "tagPhoto found :: " + tagPhoto.getNameOfTags());
//                    databasePhotoTags.add(tagPhoto);
//                }
//                return databasePhotoTags;
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
}
