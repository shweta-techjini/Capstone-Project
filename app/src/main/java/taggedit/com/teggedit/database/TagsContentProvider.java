package taggedit.com.teggedit.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/21/17.
 */

public class TagsContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    //    static final int ALL_PHOTO_WITH_TAGS = 100;
    static final int PHOTO_ONE_TAG = 101;
    //    static final int ALL_TAGS = 102;
    static final int PHOTO_TAG = 103;
    static final int TAG = 104;

    private static final SQLiteQueryBuilder sPhotoTagsQueryBuilder;

    static {
        sPhotoTagsQueryBuilder = new SQLiteQueryBuilder();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PhotoTagsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PhotoTagsContract.TAGS_PATH, TAG);
        matcher.addURI(authority, PhotoTagsContract.PHOTO_WITH_TAG_PATH + "/#", PHOTO_ONE_TAG);
        matcher.addURI(authority, PhotoTagsContract.PHOTO_WITH_TAG_PATH, PHOTO_TAG);
        return matcher;
    }

    private DatabaseHelper openHelper;

    @Override
    public boolean onCreate() {
        openHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        MyLogger.d(this, "query uri :: " + uri);
        MyLogger.d(this, "query uri matcher :: " + sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
            case PHOTO_TAG:
                sPhotoTagsQueryBuilder.setTables(PhotoTagsContract.PhotoTagEntry.TABLE_NAME);
                break;
            case TAG:
                sPhotoTagsQueryBuilder.setTables(PhotoTagsContract.TagsEntry.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        retCursor = sPhotoTagsQueryBuilder.query(openHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHOTO_ONE_TAG:
                return PhotoTagsContract.PhotoTagEntry.CONTENT_ITEM_TYPE;
            case PHOTO_TAG:
                return PhotoTagsContract.PhotoTagEntry.CONTENT_TYPE;
            case TAG:
                return PhotoTagsContract.TagsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID;
        switch (sUriMatcher.match(uri)) {
            case PHOTO_TAG:
                try {
                    rowID = openHelper.getWritableDatabase().insert(PhotoTagsContract.PhotoTagEntry.TABLE_NAME, "", contentValues);
                    if (rowID > 0) {

                        Uri _uri = ContentUris.withAppendedId(PhotoTagsContract.PhotoTagEntry.CONTENT_URI, rowID);
                        getContext().getContentResolver().notifyChange(_uri, null);
                        return _uri;
                    }
                } catch (SQLiteConstraintException e) {
                    return null;
                }
                break;
            case TAG:
                rowID = openHelper.getWritableDatabase().insert(PhotoTagsContract.TagsEntry.TABLE_NAME, "", contentValues);
                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(PhotoTagsContract.TagsEntry.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new SQLException("Failed to add a record into " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case PHOTO_TAG:
                count = openHelper.getWritableDatabase().delete(PhotoTagsContract.PhotoTagEntry.TABLE_NAME, whereClause, whereArgs);
                MyLogger.d(this, "delete uri is :: " + uri.toString() + ", deleted item count is :: " + count);
                break;
            case TAG:
                count = openHelper.getWritableDatabase().delete(PhotoTagsContract.TagsEntry.TABLE_NAME, whereClause, whereArgs);
                MyLogger.d(this, "delete tag uri is :: " + uri.toString() + ", deleted item count is :: " + count);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int count;
        switch (sUriMatcher.match(uri)) {
            case PHOTO_ONE_TAG:

            case PHOTO_TAG:
                count = openHelper.getWritableDatabase().update(PhotoTagsContract.PhotoTagEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                MyLogger.d(this, "update uri is :: " + uri.toString() + ", update item count is :: " + count);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TAG:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PhotoTagsContract.TagsEntry.TABLE_NAME, null, value);
                        MyLogger.d(this, "bulkInsert insert id is:: " + _id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
