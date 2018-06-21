package priseceanpaul.gmail.com.licenta.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import priseceanpaul.gmail.com.licenta.R;
import priseceanpaul.gmail.com.licenta.helper.CSVReader;
import priseceanpaul.gmail.com.licenta.model.Item;
import priseceanpaul.gmail.com.licenta.model.User;
import priseceanpaul.gmail.com.licenta.sql.DatabaseHelper;

import static android.content.ContentValues.TAG;

public class ImportGoogleDriveActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final String MIME_CSV="text/csv";
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
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        driveId -> retrieveContents(driveId.asDriveFile()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No file selected", e);
                    Toast.makeText(this, getString(R.string.no_file_selected),Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    protected Task<DriveId> pickTextFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_CSV))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();
        return pickItem(openOptions);
    }

    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }
    private void retrieveContents(DriveFile file) {

        Task<DriveContents> openFileTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);

        openFileTask
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    CSVReader csvReader = new CSVReader(
                            new BufferedReader(new InputStreamReader(contents.getInputStream())));

                    String[] temp;
                    while ((temp = csvReader.readNext())!=null){
                        databaseHelper.addPassword(new Item(temp));
                    }

                    Task<Void> discardTask = mDriveResourceClient.discardContents(contents);


                    return discardTask;
                })
                .addOnFailureListener(e -> {

                    Log.e(TAG, "Unable to read contents", e);

                    finish();

                })
                .addOnCompleteListener(task -> {
                    createAlertDial(file);
                });

    }

    private void deleteFile(DriveFile file){
        mDriveResourceClient
                .delete(file)
                .addOnSuccessListener(this,
                        aVoid -> {
                            Toast.makeText(this, getString(R.string.success_delete), Toast.LENGTH_LONG).show();
                            finish();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to delete file", e);;
                    finish();
                });
    }

    private void createAlertDial(DriveFile file){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.success_import));
        builder.setMessage(getString(R.string.delete_question));
        builder.setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
            deleteFile(file);
            finish();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
            //do nothing
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        onDriveClientReady();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e(TAG, "Sign-in failed.");
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
