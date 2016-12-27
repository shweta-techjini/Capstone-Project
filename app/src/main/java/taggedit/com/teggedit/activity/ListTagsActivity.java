package taggedit.com.teggedit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.adapter.TagListAdapter;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.database.TagsCurdHelper;
import taggedit.com.teggedit.databinding.ActivityListTagsBinding;
import taggedit.com.teggedit.listener.TagAdapterEventListener;
import taggedit.com.teggedit.model.Tag;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/7/17.
 */

public class ListTagsActivity extends AppCompatActivity implements View.OnClickListener, TagAdapterEventListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private SearchView mSearchView;
    private MenuItem searchMenuItem;

    private TagListAdapter tagListAdapter;
    private Dialog showAlertDialog;
    private ActivityListTagsBinding activityListTagsBinding;

    private static final int ALL_TAGS_LOADER = 0;
    private static final int SEARCH_TAGS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListTagsBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_tags);
        tagListAdapter = new TagListAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        activityListTagsBinding.tagList.setLayoutManager(linearLayoutManager);
        activityListTagsBinding.tagList.setAdapter(tagListAdapter);

        activityListTagsBinding.addTagFab.setOnClickListener(this);
        getLoaderManager().initLoader(ALL_TAGS_LOADER, null, this);
        setEmptyMessege();
    }

    @Override
    public void onClick(View view) {
        // Display a dialog to create tags
        switch (view.getId()) {
            case R.id.add_tag_fab:
                createTagDialog();
                break;
            case R.id.add_action:
                addTagInList();
                break;
            case R.id.cancel_action:
                showAlertDialog.dismiss();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_tag_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search_tag);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    private void setEmptyMessege() {
        if (tagListAdapter.getItemCount() == 0) {
            activityListTagsBinding.emptyMessage.setVisibility(View.VISIBLE);
            activityListTagsBinding.tagList.setVisibility(View.INVISIBLE);
        } else {
            activityListTagsBinding.emptyMessage.setVisibility(View.INVISIBLE);
            activityListTagsBinding.tagList.setVisibility(View.VISIBLE);
        }
    }

    public void addTagInList() {
        String tagString = ((EditText) showAlertDialog.findViewById(R.id.tags_edittext)).getText().toString();
        MyLogger.d(this, "tag name is :: " + tagString);
        // split the string "," and then create multiple tags
        if (tagString != null && tagString.trim().length() > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(tagString, ",");
            if (stringTokenizer.countTokens() > 0) {
                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken().trim().toLowerCase();
                    MyLogger.d(this, "token is :: " + token);
                    Tag tag = new Tag(token);
                    if (tagListAdapter.isAlreadyExist(tag)) {
                        // show toast that it is already exist in the list
                        Toast.makeText(this, tag.getName() + " Tag already exist, please enter different tag", Toast.LENGTH_SHORT).show();
                    } else {
                        long tagId = TagsCurdHelper.insertTag(tag, getApplicationContext());
                        tag.setId(tagId);
                        tagListAdapter.addTag(tag);
                        // insert into database and then add it to list
                    }
                }
            }
        }
        ((EditText) showAlertDialog.findViewById(R.id.tags_edittext)).setText("");
        showAlertDialog.dismiss();
        setEmptyMessege();
    }

    public void createTagDialog() {
        if (showAlertDialog == null) {
            showAlertDialog = new Dialog(this);
            showAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            showAlertDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            Window window = showAlertDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);
            showAlertDialog.setContentView(R.layout.create_tag_dialog);
        }
        showAlertDialog.findViewById(R.id.add_action).setOnClickListener(this);
        showAlertDialog.findViewById(R.id.cancel_action).setOnClickListener(this);
        showAlertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showAlertDialog != null && showAlertDialog.isShowing()) {
            showAlertDialog.dismiss();
            showAlertDialog = null;
        }
        tagListAdapter = null;
    }

    @Override
    public void deleteTag(final int position) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Are you sure?");
        alertBuilder.setMessage("By deleting this you won't be able to search using this tag any more. You might have few pics with this tag");
        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyLogger.d(this, "position clicked :: " + i);
                TagsCurdHelper.deleteTag(tagListAdapter.getItemAtPostion(position), getApplicationContext());
                tagListAdapter.removeTag(position);
                setEmptyMessege();
                dialogInterface.dismiss();
            }
        };
        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyLogger.d(this, "position clicked :: " + i);
                dialogInterface.dismiss();
            }
        };
        alertBuilder.setPositiveButton(R.string.delete, positive);
        alertBuilder.setNegativeButton(R.string.cancel, negative);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText != null && newText.length() > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("search_text", newText);
            getLoaderManager().restartLoader(SEARCH_TAGS_LOADER, bundle, this);
        } else {
            getLoaderManager().restartLoader(ALL_TAGS_LOADER, null, this);
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case ALL_TAGS_LOADER: {
                Uri tagUri = PhotoTagsContract.TagsEntry.CONTENT_URI;
                MyLogger.d(this, "tags uri is :: " + tagUri);
                String sortOrder = PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME + " ASC";
                return new CursorLoader(this,
                        tagUri,
                        null,
                        null,
                        null,
                        sortOrder);
            }
            case SEARCH_TAGS_LOADER:
                String search = bundle.getString("search_text");
                Uri tagUri = PhotoTagsContract.TagsEntry.CONTENT_URI;
                MyLogger.d(this, "search based on this text :: " + search);
                String sortOrder = PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME + " ASC";
                return new CursorLoader(this,
                        tagUri,
                        null,
                        PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME + " LIKE \'%" + search + "%\'",
                        null,
                        sortOrder);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            MyLogger.d(this, "cursor is not null " + cursor.getCount());
            ArrayList<Tag> tagsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                Tag tag = new Tag();
                tag.setId(cursor.getInt(cursor.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_ID)));
                tag.setName(cursor.getString(cursor.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME)));
                MyLogger.d(this, "tag is " + tag.toString());
                tagsList.add(tag);
            }
            tagListAdapter.setTagsList(tagsList);
            setEmptyMessege();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("reload_tag_loader", true);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean savedState = savedInstanceState.getBoolean("reload_tag_loader", false);
        if (savedState) {
            getLoaderManager().restartLoader(ALL_TAGS_LOADER, null, this);
        }
    }
}
