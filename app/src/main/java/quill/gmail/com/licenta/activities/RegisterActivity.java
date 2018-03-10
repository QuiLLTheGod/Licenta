package quill.gmail.com.licenta.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.InputValidation;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

/**
 * Created by Paul on 1/4/2018.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditName;
    private TextInputEditText textInputEditEmail;
    private TextInputEditText textInputEditPassword;
    private TextInputEditText textInputEditConfirmPassword;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private User user;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        
        initViews();
        initListeners();
        initObjects();
    }

    private void initViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditEmail = (TextInputEditText)findViewById(R.id.textInputEditTextEmail);
        textInputEditPassword = (TextInputEditText) findViewById(R.id.textInputEditPassword);
        textInputEditConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        appCompatButtonRegister  = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLinkLogin);
    }

    private void initListeners() {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);
    }

    private void initObjects() {
        inputValidation = new InputValidation(activity);
        user = new User();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;
            case R.id.appCompatTextViewLinkLogin:
                finish();
                break;
        }
    }
    private void postDataToSQLite(){

        databaseHelper = new DatabaseHelper(activity, textInputEditName.getText().toString().trim());

        if(!inputValidation.isinputEditTextFilled(textInputEditName, textInputLayoutName, getString(R.string.error_message_name))){
            return;
        }
        if(!inputValidation.isinputEditTextFilled(textInputEditEmail, textInputLayoutEmail, getString(R.string.error_message_email))){
            return;
        }
        if(!inputValidation.isinputEditTextFilled(textInputEditPassword, textInputLayoutPassword, getString(R.string.error_message_password))){
            return;
        }

        if(!inputValidation.isInputEditTextEmail(textInputEditEmail, textInputLayoutEmail, getString(R.string.error_message_email))){
            return;
        }

        if(!inputValidation.isInputEditTextMatches(textInputEditPassword, textInputLayoutConfirmPassword, textInputEditConfirmPassword,  getString(R.string.error_password_match))){
            return;
        }
        if(!databaseHelper.checkUser(textInputEditEmail.getText().toString().trim())){

            user.setEmail(textInputEditEmail.getText().toString().trim());
            //user.setName(textInputEditName.getText().toString().trim());
            user.setPassword(textInputEditPassword.getText().toString().trim());

            databaseHelper.addUser(user);

            Snackbar.make(nestedScrollView, getString(R.string.succes_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
        }
        else{
            Snackbar.make(nestedScrollView, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show();
        }
    }
    private void emptyInputEditText(){
        textInputEditName.setText(null);
        textInputEditPassword.setText(null);
        textInputEditEmail.setText(null);
        textInputEditConfirmPassword.setText(null);
    }
}
