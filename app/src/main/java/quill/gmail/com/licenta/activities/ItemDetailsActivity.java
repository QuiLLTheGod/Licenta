package quill.gmail.com.licenta.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.fragments.FingerprintDialogFragment;
import quill.gmail.com.licenta.fragments.FingerprintDialogFragment;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;
import android.view.Menu;
import android.view.MenuItem;

public class ItemDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PASSWORD_MIN_LENGTH = "AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private Button buttonDelete;
    private Button buttonShowPassword;
    private Button buttonEdit;
    private TextView textViewUsedfor;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private EditText editTextDetails;


    private DatabaseHelper databaseHelper;
    private String id;
    private String password;
    private String details;
    private String username;
    private String usedfor;
    private Bundle extras;

    private static int CODE_AUTHENTICATION_VERIFICATION=241;
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    public static final String DEFAULT_KEY_NAME = "default_key";

    private Cipher defaultCipher;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        extras = getIntent().getExtras();

        context = getApplicationContext();

        if(extras!=null){
            username = extras.getString("Username");
            details = extras.getString("Details");
            id = String.valueOf(extras.getInt("ID"));
            password =  extras.getString("Password");
            usedfor = extras.getString("UsedFor");
        }



        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        Cipher cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        keyguardManager = getSystemService(KeyguardManager.class);

        fingerprintManager = getSystemService(FingerprintManager.class);



        initViews();
        initListeners();
    }
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    private void initViews(){
        buttonShowPassword = findViewById(R.id.buttonIDAShowPassword);
        buttonDelete = findViewById(R.id.buttonItemDetailsDelete);
        buttonEdit = findViewById(R.id.buttonIDAEdit);

        editTextDetails = findViewById(R.id.editTextIDADetails);
        editTextUsername = findViewById(R.id.editTextIDAUsername);
        editTextPassword = findViewById(R.id.editTextIDAPassword);

        textViewUsedfor = findViewById(R.id.textViewIDAUsedFor);

        textViewUsedfor.setText(usedfor);

        editTextDetails.setText(details);

        editTextPassword.setClickable(false);
        editTextPassword.setEnabled(false);
        editTextPassword.setText(PASSWORD_MIN_LENGTH);
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        editTextUsername.setClickable(false);
        editTextUsername.setEnabled(false);
        editTextUsername.setText(PASSWORD_MIN_LENGTH);
        editTextUsername.setTransformationMethod(new PasswordTransformationMethod());
    }
    private void initListeners(){

        buttonDelete.setOnClickListener(this);
        buttonShowPassword.setOnClickListener( new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
    }

    public void showUserAndPassword(){
        editTextUsername.setText(username);
        editTextPassword.setText(password);
        editTextUsername.setTransformationMethod(null);
        editTextPassword.setTransformationMethod(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonItemDetailsDelete:
                createAlertDial();
                break;
        }
    }

    private class PurchaseButtonClickListener implements View.OnClickListener {

        Cipher mCipher;
        String mKeyName;

        PurchaseButtonClickListener(Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }

        @Override
        public void onClick(View view) {

            // Set up the crypto object for later. The object will be authenticated by use
            // of the fingerprint.

            if(!fingerprintManager.isHardwareDetected()){
                gotoBackup();
                return;
            }

            if (!keyguardManager.isKeyguardSecure()) {
                // Show a message that the user hasn't set up a fingerprint or lock screen.
                Toast.makeText(context,
                        "Secure lock screen hasn't set up.\n"
                                + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                        Toast.LENGTH_LONG).show();

                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {

                // This happens when no fingerprints are registered.
                Toast.makeText(context,
                        "Go to 'Settings -> Security -> Fingerprint' and register at least one" +
                                " fingerprint",
                        Toast.LENGTH_LONG).show();
                return;
            }
            createKey(DEFAULT_KEY_NAME, true);
            createKey(KEY_NAME_NOT_INVALIDATED, false);

            if (initCipher(mCipher, mKeyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or you can fall back to using a server-side verified password.
                FingerprintDialogFragment fragment
                        = new FingerprintDialogFragment();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(
                            FingerprintDialogFragment.Stage.FINGERPRINT);
                } else {
                    fragment.setStage(
                            FingerprintDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                FingerprintDialogFragment fragment
                        = new FingerprintDialogFragment();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        FingerprintDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }



    public void gotoBackup(){
        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        if(km.isKeyguardSecure()) {

            Intent i = km.createConfirmDeviceCredentialIntent("Authentication required", "password");
            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
        }
        else
            Toast.makeText(this, "No any security setup done by user(pattern or password or pin or fingerprint", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==CODE_AUTHENTICATION_VERIFICATION)
        {
            showUserAndPassword();
            Toast.makeText(this, "Success: Verified user's identity", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Failure: Unable to verify user's identity", Toast.LENGTH_SHORT).show();
        }
    }

    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void createAlertDial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("");
        builder.setMessage("Are you sure you want to delete this password? This action cannot be undone!");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postDataToSQL();
                setResult(RESULT_OK, null);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_export:
                Intent intent2 = new Intent(this, ExportGoogleDriveActivity.class);
                startActivity(intent2);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void postDataToSQL(){
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        databaseHelper.deletePassword(password, id);
    }
}
