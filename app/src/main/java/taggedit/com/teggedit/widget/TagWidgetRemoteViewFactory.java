package taggedit.com.teggedit.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/24/17.
 */

public class TagWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor itemData;

    public TagWidgetRemoteViewFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {
        MyLogger.d(this, "onCreate called");
        initChange();
    }

    @Override
    public void onDataSetChanged() {
        MyLogger.d(this, "onDataSetChanged");
        initChange();
    }

    private void initChange() {
        final long identityToken = Binder.clearCallingIdentity();
        itemData = mContext.getContentResolver().query(PhotoTagsContract.TagsEntry.CONTENT_URI, null,
                null,
                null,
                null);
        if (itemData != null)
        // Restore the identity
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return itemData == null ? 0 : itemData.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || itemData == null || !itemData.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout);
        remoteViews.setTextViewText(R.id.tag_name, itemData.getString(itemData.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME)));

        Intent intent = new Intent();
        intent.putExtra(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME, itemData.getString(itemData.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME)));
        remoteViews.setOnClickFillInIntent(R.id.list_row, intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (itemData.moveToPosition(position)) {
            return itemData.getLong(itemData.getColumnIndexOrThrow(PhotoTagsContract.TagsEntry.COLUMN_TAG_ID));
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
