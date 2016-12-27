package taggedit.com.teggedit.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Shweta on 1/21/17.
 */

public class PhotoTagsContract {

    public static final String CONTENT_AUTHORITY = "taggedit.com.teggedit.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String TAGS_PATH = "tags";
    public static final String PHOTO_WITH_TAG_PATH = "photowithtag";

    public static final class TagsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TAGS_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TAGS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TAGS_PATH;

        // Table name
        public static final String TABLE_NAME = "tags";
        public static final String COLUMN_TAG_ID = "tag_id";
        public static final String COLUMN_TAG_NAME = "tag_name";

        public static Uri buildTagUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class PhotoTagEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PHOTO_WITH_TAG_PATH).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PHOTO_WITH_TAG_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PHOTO_WITH_TAG_PATH;

        // Table name
        public static final String TABLE_NAME = "phototag";
        public static final String COLUMN_PHOTO_ID = "photo_id";
        public static final String COLUMN_PHOTO_PATH = "photo_path";
        public static final String COLUMN_PHOTO_TAG_IDS = "photo_tag_ids";
        public static final String COLUMN_PHOTO_TAG_NAMES = "photo_tag_names";

        public static Uri buildPhotoTagUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
