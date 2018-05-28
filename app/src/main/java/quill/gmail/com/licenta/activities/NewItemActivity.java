package quill.gmail.com.licenta.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.Encryptor;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class NewItemActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonInsert;
    private EditText editTextUsername;
    private EditText editTextDescription;
    private Spinner spinner;
    private EditText editTextPassword;
    private Button buttonGenerate;
    private EditText editTextUsedFor;
    private View view;
    private TypedArray icons;

    private String password = null;
    private DatabaseHelper databaseHelper;
    private Item item;
    private byte[] saltData;

    private void initViews(){

        view = findViewById(R.id.activity_new_item);

        buttonCancel = findViewById(R.id.buttonCancelNIA);
        buttonGenerate = findViewById(R.id.buttonGenerateNIA);
        buttonSave = findViewById(R.id.buttonSaveNIA);
        buttonInsert = findViewById(R.id.buttonInsertNIA);
        editTextUsedFor = findViewById(R.id.editTextUsedForNIA);
        editTextUsername = findViewById(R.id.editTextUsernameNIA);
        editTextDescription = findViewById(R.id.editTextDescriptionNIA);
        spinner = findViewById(R.id.spinnerUsedForNIA);
        editTextPassword = findViewById(R.id.editTextPasswordNIA);
        editTextPassword.setClickable(false);
        editTextPassword.setEnabled(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        icons = this.getResources().obtainTypedArray(R.array.choices_drawables_values);
    }

    private void initListeners(){
        buttonSave.setOnClickListener(this);
        buttonGenerate.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonInsert.setOnClickListener(this);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        initViews();
        initListeners();
        initObjects();

    }
    private void initObjects(){

        item = new Item();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCancelNIA:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.buttonSaveNIA:
                setResult(RESULT_OK);
                if(checkSave())
                    finish();
                break;
            case R.id.buttonGenerateNIA:
                Intent intent = new Intent(getApplicationContext(), NewPasswordActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.buttonInsertNIA:
                createAlertDial();

                break;
        }
    }

    private void createAlertDial() {
        EditText editText = new EditText(this);
        editText.setPadding(5,0,5,0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("");
        builder.setView(editText);
        builder.setMessage("Insert your password here:");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            password = editText.getText().toString();
            refreshGUI();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> password = editText.getText().toString());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            password = data.getStringExtra("generatedPassword");
            refreshGUI();
        }
    }

    protected boolean checkSave(){

        if(!editTextPassword.getText().toString().isEmpty())
            password = editTextPassword.getText().toString().trim();
        else{
            Snackbar.make(view, "Please insert your password or generate a new one", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(!editTextUsername.getText().toString().isEmpty()){
            item.setUsername(editTextUsername.getText().toString());
        }
        else{
            Snackbar.make(view, "Please input the account name you want the password to be associated with", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(!editTextDescription.getText().toString().isEmpty()){
            item.setDescription(editTextDescription.getText().toString().trim());
        }
        else {
            Snackbar.make(view, "Please add some details so you remember what the password is for", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(!editTextUsedFor.getText().toString().isEmpty()){
            item.setUsedFor(editTextUsedFor.getText().toString().trim());
        }
        else {
            Snackbar.make(view, "Please specify what the password is used for", Snackbar.LENGTH_LONG).show();
            return false;
        }
        postDataToSQL();
        return true;
    }

    protected void refreshGUI(){
        editTextPassword.setText(password);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item.setImageID(icons.getResourceId(position, 0));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        item.setImageID(icons.getResourceId(icons.length(), -1));
    }

    private void postDataToSQL() {
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            try {
                Encryptor encryptor = new Encryptor();
                encryptor.encryptText(password);
                saltData= encryptor.getEncryption();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        item.setSalt(saltData);
        databaseHelper.addPassword(item);
    }
}

