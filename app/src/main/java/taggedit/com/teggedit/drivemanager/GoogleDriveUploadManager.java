package taggedit.com.teggedit.drivemanager;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.utility.MyLogger;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Shweta on 1/24/17.
 */

public class GoogleDriveUploadManager extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private String photoPath;
    private String photoName;

    public GoogleDriveUploadManager() {
        super("GoogleDriveUploadManager");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLogger.d(this, "onStartCommand");
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        photoPath = intent.getStringExtra(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_PATH);
        photoName = intent.getStringExtra(PhotoTagsContract.PhotoTagEntry.COLUMN_PHOTO_TAG_NAMES);

        MyLogger.d(this, "photo path is " + photoPath);
        MyLogger.d(this, "photo name is " + photoName);

        if (mGoogleApiClient == null) {
            MyLogger.d(this, "create google api client");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.d(this, "onDestroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void saveFileToDrive(Bitmap mBitmapToSave) {
        // Start by creating a new contents, and setting a callback.
        MyLogger.i(this, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            MyLogger.i(this, "Failed to create new contents.");
                            return;
                        }
                        // Otherwise, we can write our data to the new contents.
                        MyLogger.i(this, "New contents created.");
                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle(photoName + ".png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSender(intentSender, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MyLogger.i(this, "API client connected.");
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

        saveFileToDrive(bitmap);
    }

    @Override
    public void onConnectionSuspended(int i) {
        MyLogger.i(this, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyLogger.i(this, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            Toast.makeText(this, getString(R.string.error_message) + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
