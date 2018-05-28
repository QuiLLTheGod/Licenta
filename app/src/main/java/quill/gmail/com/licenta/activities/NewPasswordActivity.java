package quill.gmail.com.licenta.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
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
    private View view;

    private int seekBarLength;
    private byte[] saltData;

    private TextView lengthTextView;


    private Generator generator;

    private void initViews(){

        view = findViewById(R.id.activity_new_password);

        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setEnabled(false);
        buttonCancel = findViewById(R.id.buttonCancelNIA);
        buttonGenerate = findViewById(R.id.buttonGenerateNIA);

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
            case R.id.buttonCancelNIA:
                setResult(RESULT_OK,null);
                finish();
                break;

            case R.id.buttonConfirm:
                Intent returnedIntent = new Intent();
                returnedIntent.putExtra("generatedPassword", password);
                setResult(RESULT_OK,returnedIntent);
                finish();
                break;

            case R.id.buttonGenerateNIA:
                createGenerator();
                break;
        }
    }

    private void createGenerator() {
        if(!highcharSwitch.isChecked() && !lowcharSwitch.isChecked() && !specialSwitch.isChecked() && (numberSeekbar.getProgress() == 0)) {
            Snackbar.make(view, getString(R.string.please_select), Snackbar.LENGTH_LONG).show();
            return;
        }
        if(seekBarLength == 0) {
            Snackbar.make(view, getString(R.string.no_input), Snackbar.LENGTH_LONG).show();
            return;
        }
        if(seekBarLength < numberSeekbar.getProgress()) {
            Snackbar.make(view, getString(R.string.too_many_numbers), Snackbar.LENGTH_LONG).show();
            return;
        }
        else {
        generator = new Generator(highcharSwitch.isChecked(), lowcharSwitch.isChecked(),
                specialSwitch.isChecked(), numberSeekbar.getProgress() , seekBarLength);
        password = generator.getPassword();
        textViewGenPass.setText(password);
        buttonConfirm.setEnabled(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.numbersSwitch:
                if(b)
                    numberSeekbar.setEnabled(true);
                else {
                    numberSeekbar.setEnabled(false);
                    numberSeekbar.setProgress(0);
                    numbersTextView.setText(getString(R.string.nrOFnr));
                }
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
