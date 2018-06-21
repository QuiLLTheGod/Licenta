package priseceanpaul.gmail.com.licenta.activities;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import priseceanpaul.gmail.com.licenta.R;
import priseceanpaul.gmail.com.licenta.fragments.PasswordsAdapter;
import priseceanpaul.gmail.com.licenta.model.Item;
import priseceanpaul.gmail.com.licenta.model.User;
import priseceanpaul.gmail.com.licenta.sql.DatabaseHelper;

public class ItemsListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayListOfItems;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton buttonNewPassword;

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



    private void initViews(){
        recyclerView = findViewById(R.id.rv);
        buttonNewPassword = findViewById(R.id.buttonNewPassword);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        arrayListOfItems = databaseHelper.getItems();

        setContentView(R.layout.activity_items_list);
        initViews();

        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLayoutManager);
        PasswordsAdapter adapter = new PasswordsAdapter(this, arrayListOfItems);
        recyclerView.setAdapter(adapter);
        setListeners();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, ItemsListActivity.class);
            startActivity(refresh);
            this.finish();
        }
        else{
            Intent refresh = new Intent(this, ItemsListActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }

    private void setListeners() {
        buttonNewPassword.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonNewPassword:
                Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                startActivityForResult(intent,1);
                break;
            /*
             *   Maybe add extra features?
             *
             * */
            default:
                break;
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

    public void initKeyStore(){
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        initKeyStore();
        switch (id){
            case R.id.action_import:
                Intent intent3 = new Intent(this, ImportGoogleDriveActivity.class);
                startActivity(intent3);
                break;
            case R.id.log_out:
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

}
