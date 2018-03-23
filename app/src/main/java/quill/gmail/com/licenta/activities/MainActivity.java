package quill.gmail.com.licenta.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.helper.InputValidation;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = MainActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextUser;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initViews();
        initListeners();
        initObjects();
    }

    /*
    *   Initialize the views for the login activity.
    *
    * */


    private void initViews(){
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextUser  = (TextInputEditText) findViewById(R.id.textInputEditTextUser);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);
    }
    private void initListeners(){
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects(){
        inputValidation = new InputValidation(activity);

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }
    private void verifyFromSQLite(){
        databaseHelper = new DatabaseHelper(activity, textInputEditTextUser.getText().toString().trim());
/*
        if(!inputValidation.isinputEditTextFilled(textInputEditTextUser, textInputLayoutEmail, getString(R.string.error_email_exists)))
            return;

        if(!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email)))
        return;

        if(!inputValidation.isinputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password)))
            return;
  */
        if(databaseHelper.checkUser(textInputEditTextUser.getText().toString().trim(),
                                    textInputEditTextPassword.getText().toString().trim())){
            User.NAME = textInputEditTextUser.getText().toString().trim();
            Intent accountsIntent = new Intent(activity, UsersActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextUser.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
        }
        else{
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_username_password), Snackbar.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText(){
        textInputEditTextUser.setText(null);
        textInputEditTextPassword.setText(null);
    }
}
