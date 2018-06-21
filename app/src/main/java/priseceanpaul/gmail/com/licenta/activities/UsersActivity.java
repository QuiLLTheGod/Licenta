package priseceanpaul.gmail.com.licenta.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import priseceanpaul.gmail.com.licenta.model.Item;
import priseceanpaul.gmail.com.licenta.sql.DatabaseHelper;

/**
 * Created by Paul on 1/4/2018.
 */

public class UsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView textViewName;

    private FloatingActionButton buttonNewPassword;
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private int[] passwordIDs;
    private ArrayList<Item> arrayListOfItems;

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

    protected void onCreate(Bundle savedInstanceStates){
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_users);

        buttonNewPassword = findViewById(R.id.buttonNewPassword);
        listView = findViewById(R.id.User_ListView);
        //passwordIDs = databaseHelper.getPasswordIDs();
        ArrayList<String> strings = new ArrayList<>();
        for (Item i: arrayListOfItems) {
            strings.add(i.getUsername());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UsersActivity.this,
                android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(arrayAdapter);
        setListeners();
    }
    private void setListeners(){
        buttonNewPassword.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, UsersActivity.class);
            startActivity(refresh);
            this.finish();
        }
        else{
            Intent refresh = new Intent(this, UsersActivity.class);
            startActivity(refresh);
            this.finish();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(UsersActivity.this, ItemDetailsActivity.class);
        intent.putExtra("ID", arrayListOfItems.get(i).getId());
        intent.putExtra("Password", arrayListOfItems.get(i).getDecryptedPassword());
        intent.putExtra("Username", arrayListOfItems.get(i).getUsername());
        intent.putExtra("Details", arrayListOfItems.get(i).getDescription());
        intent.putExtra("UsedFor", arrayListOfItems.get(i).getUsedFor());
        startActivityForResult(intent, 1);
    }
}
