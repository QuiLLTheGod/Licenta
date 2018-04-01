package quill.gmail.com.licenta.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.BCrypt;
import quill.gmail.com.licenta.helper.Decryptor;
import quill.gmail.com.licenta.helper.Encryptor;
import quill.gmail.com.licenta.helper.Generator;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private Button buttonConfirm;
    private Button buttonCancel;
    private EditText editTextInsertPass;
    private DatabaseHelper databaseHelper;
    private Item item;
    private SeekBar numberSeekbar;
    private SeekBar lengthSeekbar;
    private Button buttonGenerate;
    private Switch highcharSwitch;
    private Switch lowcharSwitch;
    private Switch numbersSwitch;
    private Switch specialSwitch;
    private TextView textViewGenPass;
    private TextView numbersTextView;
    private String password;

    private int seekBarLength;
    private byte[] saltData;

    private TextView lengthTextView;

    private Generator generator;

    private void initViews(){

        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonGenerate = findViewById(R.id.buttonGenerate);

        editTextInsertPass = findViewById(R.id.editTextInsertPass);

        textViewGenPass = findViewById(R.id.textViewGenerated);

        highcharSwitch = findViewById(R.id.highcharSwitch);
        lowcharSwitch = findViewById(R.id.lowcharSwitch);
        numbersSwitch = findViewById(R.id.numbersSwitch);
        specialSwitch = findViewById(R.id.specialSwitch);

        numberSeekbar = findViewById(R.id.numbersSeekbar);
        numberSeekbar.setEnabled(false);

        lengthSeekbar = findViewById(R.id.lengthSeekbar);
        numbersTextView = findViewById(R.id.numbersView);

        lengthTextView = findViewById(R.id.lengthTextView);
    }

    private void initListeners(){
        buttonConfirm.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonGenerate.setOnClickListener(this);

        highcharSwitch.setOnCheckedChangeListener(this);
        lowcharSwitch.setOnCheckedChangeListener(this);
        specialSwitch.setOnCheckedChangeListener(this);
        numbersSwitch.setOnCheckedChangeListener(this);

        numberSeekbar.setOnSeekBarChangeListener(this);
        lengthSeekbar.setOnSeekBarChangeListener(this);
    }

    private void initObjects(){

        item = new Item();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initViews();
        initListeners();
        initObjects();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonCancel:
                setResult(RESULT_OK,null);
                finish();
                break;

            case R.id.buttonConfirm:
                postDataToSQL();
                setResult(RESULT_OK,null);
                finish();
                break;

            case R.id.buttonGenerate:
                createGenerator();
                break;
        }
    }

    private void createGenerator() {
        generator = new Generator(highcharSwitch.isChecked(), lowcharSwitch.isChecked(),
                specialSwitch.isChecked(), numberSeekbar.getProgress() , seekBarLength);
        password = generator.getPassword();

        textViewGenPass.setText(password);
    }

    private void postDataToSQL() {
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        item.setData(password);
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.numbersSwitch:
                if(b)
                    numberSeekbar.setEnabled(true);
                else
                    numberSeekbar.setEnabled(false);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.numbersSeekbar:
                numbersTextView.setText(getResources().getString(R.string.nrOFnr)+String.valueOf(i));
                break;
            case R.id.lengthSeekbar:
                seekBarLength = i;
                lengthTextView.setText(getResources().getString(R.string.password_length) + String.valueOf(i));
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
