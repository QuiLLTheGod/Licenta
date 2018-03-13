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

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.Generator;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

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
    private String password;

    private Generator generator;

    private void initViews(){

        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonGenerate = findViewById(R.id.buttonGenerate);

        editTextInsertPass = (EditText) findViewById(R.id.editTextInsertPass);

        textViewGenPass = findViewById(R.id.textViewGenerated);

        highcharSwitch = findViewById(R.id.highcharSwitch);
        lowcharSwitch = findViewById(R.id.lowcharSwitch);
        numbersSwitch = findViewById(R.id.numbersSwitch);
        specialSwitch = findViewById(R.id.specialSwitch);

        numberSeekbar = findViewById(R.id.numbersSeekbar);
        lengthSeekbar = findViewById(R.id.lengthSeekbar);
    }

    private void initListeners(){
        buttonConfirm.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonGenerate.setOnClickListener(this);

        highcharSwitch.setOnCheckedChangeListener(this);
        lowcharSwitch.setOnCheckedChangeListener(this);
        specialSwitch.setOnCheckedChangeListener(this);
        numbersSwitch.setOnCheckedChangeListener(this);
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
                specialSwitch.isChecked(), 0, 5);
        generator.generatePassword();
        password = generator.getPassword();
        textViewGenPass.setText(password);
    }

    private void postDataToSQL() {
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);

        item.setData("");
        item.setPassword(password);

        databaseHelper.addPassword(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
}
