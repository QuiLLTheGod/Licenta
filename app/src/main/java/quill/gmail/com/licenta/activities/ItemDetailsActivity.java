package quill.gmail.com.licenta.activities;

import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
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


    public enum ButtonPressed{
        BUTTON_COPY,
        BUTTON_DELETE,
        BUTTON_SHOW
    }

    private static final String PASSWORD_MIN_LENGTH = "AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private Button buttonDelete;
    private Button buttonShowPassword;
    private Button buttonCopy;
    private TextView textViewUsedfor;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private EditText editTextDetails;

    private ButtonPressed buttonPressed = ButtonPressed.BUTTON_SHOW;

    private DatabaseHelper databaseHelper;
    private String id;
    private String password;
    private String details;
    private String username;
    private String usedfor;
    private Bundle extras;

    private static int CODE_AUTHENTICATION_VERIFICATION=241;
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";

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
        buttonCopy = findViewById(R.id.buttonIDACopy);
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

        editTextDetails.setEnabled(false);
        editTextDetails.setClickable(false);
    }
    private void initListeners(){

        buttonDelete.setOnClickListener(new FingerPrintButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
        buttonShowPassword.setOnClickListener( new FingerPrintButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
        buttonCopy.setOnClickListener(new FingerPrintButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
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

        }
    }

    private void copyToClipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", password);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.success_copy), Toast.LENGTH_LONG).show();
    }

    private class FingerPrintButtonClickListener implements View.OnClickListener {

        Cipher mCipher;
        String mKeyName;

        FingerPrintButtonClickListener(Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.buttonItemDetailsDelete:
                    buttonPressed = ButtonPressed.BUTTON_DELETE;
                    break;
                case R.id.buttonIDACopy:
                    buttonPressed = ButtonPressed.BUTTON_COPY;
                    break;
                case R.id.buttonIDAShowPassword:
                    buttonPressed = ButtonPressed.BUTTON_SHOW;
                    break;
            }

            if(!fingerprintManager.isHardwareDetected()){
                gotoBackup();
                return;
            }

            if (!keyguardManager.isKeyguardSecure()) {

                Toast.makeText(context,
                        "Secure lock screen nu este prezent.\n"
                                + "Du-te la 'Settings -> Security -> Fingerprint' pentru a seta amprenta",
                        Toast.LENGTH_LONG).show();

                return;
            }
            if (!fingerprintManager.hasEnrolledFingerprints()) {


                Toast.makeText(context,
                        "Du-te 'Settings -> Security -> Fingerprint' şi înregistreaă o nouă" +
                                " amprentă",
                        Toast.LENGTH_LONG).show();
                return;
            }
            createKey(DEFAULT_KEY_NAME, true);
            createKey(KEY_NAME_NOT_INVALIDATED, false);

            if (initCipher(mCipher, mKeyName)) {


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
            }
            else {

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

            Intent i = km.createConfirmDeviceCredentialIntent("Autentificare necesară", "parolă");
            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
        }
        else
            Toast.makeText(this, "Utilizatorul nu a creat nici o metodă de securitate", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==CODE_AUTHENTICATION_VERIFICATION)
        {
            doAction();
            Toast.makeText(this, "Success: Am validat identitatea utilizatorului", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Eşec: Nu am reuşit să validez identitatea utilizatorului", Toast.LENGTH_SHORT).show();
        }
    }

    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {

        try {
            mKeyStore.load(null);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);


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

    public void doAction(){
        switch (buttonPressed){
            case BUTTON_COPY:
                copyToClipboard();
                break;
            case BUTTON_SHOW:
                showUserAndPassword();
                break;
            case BUTTON_DELETE:
                createAlertDial();
                break;
        }
    }


    private void createAlertDial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("");
        builder.setMessage(getString(R.string.delete_question));
        builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
            postDataToSQL();
            setResult(RESULT_OK, null);
            finish();
        });

        builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
            //do nothing
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

        switch (id){
            case R.id.action_import:
                Intent intent3 = new Intent(this, ImportGoogleDriveActivity.class);
                startActivity(intent3);
                break;
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
