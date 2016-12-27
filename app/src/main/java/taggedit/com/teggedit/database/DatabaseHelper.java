package taggedit.com.teggedit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shweta on 1/21/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taggedit.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + PhotoTagsContract.TagsEntry.TABLE_NAME + " (" + PhotoTagsContract.TagsEntry.COLUMN_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME + " TEXT UNIQUE NOT NULL);";
        final String SQL_CREATE_PHOTO_TAG_TABLE = "CREATE TABLE " + PhotoTagsContract.PhotoTagEntry.TABLE_NAME + " (" + PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH + " TEXT UNIQUE NOT NULL, " + PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_IDS + " TEXT NOT NULL, " + PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_PHOTO_TAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
