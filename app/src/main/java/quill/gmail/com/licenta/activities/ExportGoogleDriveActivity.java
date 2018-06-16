package quill.gmail.com.licenta.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;

import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.CSVwriter;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by Paul on 5/6/2018.
 */

public class ExportGoogleDriveActivity extends Activity {

    private static final String MIME_CSV="text/csv";
    private static final String DB_LOCATION = "/data/data/quill.gmail.com.licenta/databases/" + User.NAME + ".db";
    private static final String MIME = "application/x-sqlite3";


    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;

    private static final int REQUEST_CODE_CREATOR = 2;

    private DatabaseHelper databaseHelper;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        signIn();
    }

    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code " + String.valueOf(requestCode));

                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");

                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));

                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
                    saveFileToDrive();
                }
                break;
            case REQUEST_CODE_CREATOR:
                Log.i(TAG, "creator request code");

                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), getString(R.string.success_export), Toast.LENGTH_LONG);
                }
                this.finish();
                break;
        }
    }


    private void saveFileToDrive() {

        Log.i(TAG, "Creating new contents.");

        mDriveResourceClient
                .createContents()
                .continueWithTask(
                        task -> {
                           // return createFileIntentSender(task.getResult(), User.NAME, MIME, new java.io.File(DB_LOCATION));
                            return  createFileIntentSender(task.getResult(), User.NAME, MIME_CSV,sqlToCVS());
                        })
                .addOnFailureListener(
                        e -> Log.w(TAG, "Failed to create new contents.", e));
    }

    private File sqlToCVS(){
        File exportDir = new File(getCacheDir(), "");
        File file = new File(exportDir, "csvname.csv");
        ArrayList<Item> items = databaseHelper.getItems();
        try
        {
            file.createNewFile();
            CSVwriter csvWrite = new CSVwriter(new FileWriter(file));
            for(Item item:items){
                csvWrite.writeNext(item.getAll());
            }
            csvWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    private Task<Void> createFileIntentSender(DriveContents driveContents, final String titl,
                                              final String mime, final java.io.File file) {
        Log.i(TAG, "New contents created.");
        OutputStream oos = driveContents.getOutputStream();
        try{
            if (oos != null) try {
                InputStream is = new FileInputStream(file);
                byte[] buf = new byte[4096];
                int c;
                while ((c = is.read(buf, 0, buf.length)) > 0) {
                    oos.write(buf, 0, c);
                    oos.flush();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType(mime)
                        .setTitle(titl)
                        .build();
        CreateFileActivityOptions createFileActivityOptions =
                new CreateFileActivityOptions.Builder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(driveContents)
                        .build();

        return mDriveClient
                .newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith(
                        task -> {
                            startIntentSenderForResult(task.getResult(), REQUEST_CODE_CREATOR, null, 0, 0, 0);
                            return null;
                        }
                );
    }
}
