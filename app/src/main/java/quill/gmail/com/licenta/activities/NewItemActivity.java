package quill.gmail.com.licenta.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.VectorEnabledTintResources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    private TextView textViewAccountName;
    private EditText editTextAccountName;
    private EditText editTextDescription;
    private Spinner spinner;
    private EditText editTextInsertPassword;
    private Button buttonGenerate;
    private TextView textViewGenerated;
    private EditText editTextUsedFor;
    private View view;

    private String password = null;
    private DatabaseHelper databaseHelper;
    private Item item;
    private byte[] saltData;

    private void initViews(){

        view = findViewById(R.id.activity_new_item);

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonGenerate = findViewById(R.id.buttonGenerateNewItem);
        buttonSave = findViewById(R.id.buttonSave);
        editTextUsedFor = findViewById(R.id.editTextUsedFor);
        textViewAccountName = findViewById(R.id.textViewAccountName);
        editTextAccountName = findViewById(R.id.editTextAccountName);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinner = findViewById(R.id.spinnerItemDetails);
        textViewGenerated = findViewById(R.id.textViewGeneratedPass);
        editTextInsertPassword = findViewById(R.id.editTextInsertPassword);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void initListeners(){
        buttonSave.setOnClickListener(this);
        buttonGenerate.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
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
            case R.id.buttonCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.buttonSave:
                setResult(RESULT_OK);
                if(checkSave())
                    finish();
                break;
            case R.id.buttonGenerateNewItem:
                Intent intent = new Intent(getApplicationContext(), NewPasswordActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            password = data.getStringExtra("generatedPassword");
            refreshGUI();
        }
    }

    protected boolean checkSave(){
        if(textViewGenerated.getText().toString().matches("")){
            if(!editTextInsertPassword.getText().toString().isEmpty())
                password = editTextInsertPassword.getText().toString().trim();
            else{
                Snackbar.make(view, "Please insert your password or generate a new one", Snackbar.LENGTH_LONG).show();
                return false;
            }
        }
        if(!editTextAccountName.getText().toString().isEmpty()){
            item.setUsername(editTextAccountName.getText().toString());
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
        textViewGenerated.setText(password);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                editTextInsertPassword.setEnabled(false);
                editTextInsertPassword.setClickable(false);
                buttonGenerate.setEnabled(true);
                break;
            case 1:
                editTextInsertPassword.setEnabled(true);
                editTextInsertPassword.setClickable(true);
                buttonGenerate.setEnabled(false);
                textViewGenerated.setText("");

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

