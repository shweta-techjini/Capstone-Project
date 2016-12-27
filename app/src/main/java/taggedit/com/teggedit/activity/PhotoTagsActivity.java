package taggedit.com.teggedit.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.adapter.PhotoTagViewPagerAdapter;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.database.PhotoTagsCurdHelper;
import taggedit.com.teggedit.database.TagsCurdHelper;
import taggedit.com.teggedit.databinding.ActivityPhotoTagBinding;
import taggedit.com.teggedit.drivemanager.GoogleDriveUploadManager;
import taggedit.com.teggedit.fragement.PhotoAddTagFragment;
import taggedit.com.teggedit.model.Tag;
import taggedit.com.teggedit.model.TagPhoto;
import taggedit.com.teggedit.utility.MyLogger;
import taggedit.com.teggedit.utility.PathUtils;

/**
 * Created by Shweta on 1/7/17.
 */

public class PhotoTagsActivity extends AppCompatActivity {

    private ActivityPhotoTagBinding photoTagBinding;
    private Bitmap selectedBitmap;
    private String selectedImagePath;
    private PhotoTagViewPagerAdapter photoTagViewPagerAdapter;
    private ArrayList<Tag> preTags;

    public static final String PHOTO_PATH = "photo_path";
    public static final String PHOTO_TAG_IDS = "photo_tag_ids";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrash.log("PhotoTagActivity, onCreate");
        photoTagBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_tag);
        Uri selectedImage = getIntent().getData();
        if (selectedImage == null) {
            MyLogger.d(this, "No selected image uri is mentioned");

            selectedImagePath = getIntent().getStringExtra(PHOTO_PATH);
            String photoTagIds = getIntent().getStringExtra(PHOTO_TAG_IDS);
            preTags = TagsCurdHelper.getTagsFromIds(photoTagIds, this);
            MyLogger.d(this, "photo path is :: " + selectedImagePath);
            MyLogger.d(this, "photo tag ids is :: " + photoTagIds);
        } else {
            MyLogger.d(this, "uri is :: " + selectedImage);
            selectedImagePath = PathUtils.getPath(this, selectedImage);
        }
        MyLogger.d(this, "path from path utils :: " + selectedImagePath);

        selectedBitmap = BitmapFactory.decodeFile(selectedImagePath);
        photoTagBinding.selectedImage.setImageBitmap(selectedBitmap);
        photoTagViewPagerAdapter = new PhotoTagViewPagerAdapter(getSupportFragmentManager(), this);
        photoTagBinding.viewPager.setAdapter(photoTagViewPagerAdapter);

        photoTagBinding.tabLayout.setupWithViewPager(photoTagBinding.viewPager);
    }

    public Bitmap getSelectedBitmap() {
        return selectedBitmap;
    }

    public ArrayList<Tag> getPreTags() {
        return preTags;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_tag_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // item save click
        switch (item.getItemId()) {
            case R.id.action_save:
                saveAction();
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                finish();
                break;
//            case R.id.action_upload:
//                uploadToDrive();
//                Toast.makeText(this, "Uploading in background.", Toast.LENGTH_SHORT).show();
//                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadToDrive() {
        Intent intent = new Intent(this, GoogleDriveUploadManager.class);
        intent.putExtra(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH, selectedImagePath);
        MyLogger.d(this, preTags.toString());
        intent.putExtra(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES, preTags.toString());
        startService(intent);
    }

    private void saveAction() {
        PhotoAddTagFragment photoAddTagFragment = (PhotoAddTagFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":0");
        if (photoAddTagFragment != null) {
            MyLogger.d(this, "fragment id is :: " + photoAddTagFragment.getId());
            ArrayList<Tag> tags = photoAddTagFragment.getTagsForPhoto();
            if (tags != null) {
                MyLogger.d(this, "selected image path is :: " + selectedImagePath);
                StringBuilder idStringBuilder = new StringBuilder();
                StringBuilder namesStringBuilder = new StringBuilder();
                for (int i = 0; i < tags.size(); i++) {
                    idStringBuilder.append(tags.get(i).getId());
                    namesStringBuilder.append(tags.get(i).getName());
                    if (i != (tags.size() - 1)) {
                        idStringBuilder.append(",");
                        namesStringBuilder.append(",");
                    }
                }
                MyLogger.d(this, "photo related tags ids are :: " + idStringBuilder);
                MyLogger.d(this, "photo related tags names are :: " + namesStringBuilder);
                TagPhoto tagPhoto = new TagPhoto();
                tagPhoto.setPhotoPath(selectedImagePath);
                tagPhoto.setPhotoTagIds(idStringBuilder.toString());
                tagPhoto.setPhotoTagsName(namesStringBuilder.toString());
                PhotoTagsCurdHelper.insertOrUpdatePhotTag(tagPhoto, this);
            }
        } else {
            Toast.makeText(this, "Please add some tags.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
