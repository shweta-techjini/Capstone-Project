package taggedit.com.teggedit.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.crash.FirebaseCrash;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.adapter.IntroAdapter;
import taggedit.com.teggedit.databinding.ActivityIntroBinding;
import taggedit.com.teggedit.datastore.TaggedItPreference;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/6/17.
 */
public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int REQUEST_FILE = 101;
    private ActivityIntroBinding activityIntroBinding;
    public static final int READ_PERMISSION_REQUESTED = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaggedItPreference.getTaggedItPreference(this).setFreshInstall(false);
        activityIntroBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        activityIntroBinding.pager.setAdapter(new IntroAdapter(this));
        activityIntroBinding.pager.addOnPageChangeListener(this);
        activityIntroBinding.getStarted.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            boolean hideSkip = intent.getBooleanExtra(SettingsActivity.HIDE_SKIP, false);
            if (hideSkip) {
                activityIntroBinding.skip.setVisibility(View.INVISIBLE);
            } else {
                activityIntroBinding.skip.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                activityIntroBinding.imgLoading1.setImageResource(R.drawable.circle_over);
                activityIntroBinding.imgLoading2.setImageResource(R.drawable.circle_normal);
                activityIntroBinding.imgLoading3.setImageResource(R.drawable.circle_normal);
                break;
            case 1:
                activityIntroBinding.imgLoading1.setImageResource(R.drawable.circle_normal);
                activityIntroBinding.imgLoading2.setImageResource(R.drawable.circle_over);
                activityIntroBinding.imgLoading3.setImageResource(R.drawable.circle_normal);
                break;
            case 2:
                activityIntroBinding.imgLoading1.setImageResource(R.drawable.circle_normal);
                activityIntroBinding.imgLoading2.setImageResource(R.drawable.circle_normal);
                activityIntroBinding.imgLoading3.setImageResource(R.drawable.circle_over);
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_started:
                // open gallery to select photo
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

                break;
            case R.id.skip:
                startActivity(new Intent(IntroActivity.this, HomeActivity.class));
                finish();
                break;
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
                // only one photo is selected
                Uri uri = data.getData();
                FirebaseCrash.log("IntroActivity uri for is :: " + uri);
                // start phototag activity and pass data
                startPhotoTagActivity(uri);
            }
        }
    }

    private void startPhotoTagActivity(Uri selectedImageUri) {
        Intent intent = new Intent(IntroActivity.this, PhotoTagsActivity.class);
        intent.setData(selectedImageUri);
        startActivity(intent);
        finish();
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
                ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
