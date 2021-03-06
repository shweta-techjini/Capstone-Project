package taggedit.com.teggedit.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.adapter.HomePhotoGridAdapter;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.databinding.ActivityHomeBinding;
import taggedit.com.teggedit.model.TagPhoto;
import taggedit.com.teggedit.utility.MyLogger;

import static taggedit.com.teggedit.activity.IntroActivity.READ_PERMISSION_REQUESTED;
import static taggedit.com.teggedit.activity.IntroActivity.REQUEST_FILE;

/**
 * Created by Shweta on 1/7/17.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, ActivityCompat.OnRequestPermissionsResultCallback {

    private FirebaseAnalytics mFirebaseAnalytics;
    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private SearchView.OnQueryTextListener listener;
    private HomePhotoGridAdapter homePhotoGridAdapter;
    private ActivityHomeBinding activityHomeBinding;
    private static final String RELOAD_PHOTO_LOADER = "reload_photo_loader";

    private static final int ALL_PHOTO_WITH_TAGS_LOADER = 0;
    private String searchTagFromWidget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        appStarted();
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        homePhotoGridAdapter = new HomePhotoGridAdapter(this);

        activityHomeBinding.photoGrid.setAdapter(homePhotoGridAdapter);
        activityHomeBinding.photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                TagPhoto tagPhoto = homePhotoGridAdapter.getItem(position);
                Intent photoTagActivity = new Intent(HomeActivity.this, PhotoTagsActivity.class);
                photoTagActivity.putExtra(PhotoTagsActivity.PHOTO_ID, tagPhoto.getAutoIncrementId());
                photoTagActivity.putExtra(PhotoTagsActivity.PHOTO_PATH, tagPhoto.getPhotoPath());
                photoTagActivity.putExtra(PhotoTagsActivity.PHOTO_TAG_IDS, tagPhoto.getPhotoTagIds());
                startActivity(photoTagActivity);
            }
        });

        activityHomeBinding.openGallery.setOnClickListener(this);
        listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                homePhotoGridAdapter.getFilter().filter(newText);
                return false;
            }
        };

        setEmptyMessege();
        getLoaderManager().initLoader(ALL_PHOTO_WITH_TAGS_LOADER, null, this);

        Intent intent = getIntent();
        if (intent != null) {
            String tagName = intent.getStringExtra(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME);
            if (tagName != null) {
                MyLogger.d(this, "home activity tag name :: " + tagName);
                searchTagFromWidget = tagName;
            }
        }
    }

    private void setEmptyMessege() {
        if (homePhotoGridAdapter.getCount() == 0) {
            activityHomeBinding.emptyMessage.setVisibility(View.VISIBLE);
            activityHomeBinding.photoGrid.setVisibility(View.INVISIBLE);
        } else {
            activityHomeBinding.emptyMessage.setVisibility(View.INVISIBLE);
            activityHomeBinding.photoGrid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(listener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
                showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_PERMISSION_REQUESTED);
            }
        } else {
            openFileProvider();
        }
    }

    private void openFileProvider() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_chooser)), REQUEST_FILE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FILE && data != null) {
                Uri uri = data.getData();
                startPhotoTagActivity(uri);
            }
        }
    }

    private void startPhotoTagActivity(Uri selectedImageUri) {
        MyLogger.d(this, "startPhotoTagActivity is called from homeactivity");
        Intent intent = new Intent(HomeActivity.this, PhotoTagsActivity.class);
        intent.setData(selectedImageUri);
        startActivity(intent);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri photoTagUri = PhotoTagsContract.PhotoTagEntry.CONTENT_URI;
//        MyLogger.d(this, "tags uri is :: " + photoTagUri);
        return new CursorLoader(this,
                photoTagUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
//            MyLogger.d(this, "cursor is not null ");
            ArrayList<TagPhoto> tagsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                TagPhoto tagPhoto = new TagPhoto();
                tagPhoto.setAutoIncrementId(cursor.getLong(cursor.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_ID)));
                tagPhoto.setPhotoPath(cursor.getString(cursor.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH)));
                tagPhoto.setPhotoTagIds(cursor.getString(cursor.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_IDS)));
                tagPhoto.setPhotoTagsName(cursor.getString(cursor.getColumnIndex(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES)));
                tagsList.add(tagPhoto);
            }
            homePhotoGridAdapter.setItemList(tagsList);
            setEmptyMessege();
            if (searchTagFromWidget != null) {
                homePhotoGridAdapter.getFilter().filter(searchTagFromWidget);
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RELOAD_PHOTO_LOADER, true);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean savedState = savedInstanceState.getBoolean(RELOAD_PHOTO_LOADER, false);
        if (savedState) {
            getLoaderManager().restartLoader(ALL_PHOTO_WITH_TAGS_LOADER, null, this);
        }
    }

    private void appStarted() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "HomeActivity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PERMISSION_REQUESTED:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileProvider();
                } else {
                    boolean shouldShowRational = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    MyLogger.d(this, "shouldShowRational :: " + shouldShowRational);
                    if (shouldShowRational) {
                        // Show rational message or directly ask for the request.
                        showRationaleDialog();
                    }
                }
                break;
        }
    }

    private void showRationaleDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(getString(R.string.permission_denied));
        alertBuilder.setMessage(getString(R.string.rational_storage_permission));
        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyLogger.d(this, "position clicked :: " + i);
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_PERMISSION_REQUESTED);
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
        alertBuilder.setPositiveButton(R.string.retry, positive);
        alertBuilder.setNegativeButton(R.string.i_m_sure, negative);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }
}
