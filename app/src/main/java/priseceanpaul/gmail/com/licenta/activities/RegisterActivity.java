package priseceanpaul.gmail.com.licenta.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import priseceanpaul.gmail.com.licenta.R;
import priseceanpaul.gmail.com.licenta.helper.BCrypt;
import priseceanpaul.gmail.com.licenta.helper.InputValidation;
import priseceanpaul.gmail.com.licenta.model.User;
import priseceanpaul.gmail.com.licenta.sql.DatabaseHelper;

/**
 * Created by Paul on 1/4/2018.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;

    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText textInputEditName;

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
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditName = (TextInputEditText) findViewById(R.id.textInputEditTextName);

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
                if(postDataToSQLite()) {
                    finish();
                }
                break;
            case R.id.appCompatTextViewLinkLogin:
                finish();
                break;
        }
    }
    private boolean postDataToSQLite(){

        databaseHelper = new DatabaseHelper(activity, textInputEditName.getText().toString().trim());

        if(!inputValidation.isinputEditTextFilled(textInputEditName, textInputLayoutName, getString(R.string.error_message_name))){
            return false;
        }

        if(!inputValidation.isinputEditTextFilled(textInputEditPassword, textInputLayoutPassword, getString(R.string.error_message_password))){
            return false;
        }


        if(!inputValidation.isInputEditTextMatches(textInputEditPassword, textInputLayoutConfirmPassword, textInputEditConfirmPassword,  getString(R.string.error_password_match))){
            return false;
        }
        if(!databaseHelper.checkUser(textInputEditName.getText().toString().trim(),"randomPassword")){

            User.NAME = textInputEditName.getText().toString().trim();
            user.setPassword(textInputEditPassword.getText().toString().trim());
            user.setName(textInputEditName.getText().toString().trim());
            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(textInputEditPassword.getText().toString().trim(), salt);
            user.setHash(hashedPassword);
            databaseHelper.addUser(user);
            Snackbar.make(nestedScrollView, getString(R.string.succes_message), Snackbar.LENGTH_LONG).show();
            emptyInputEditText();
            return true;
        }
        else{
            Snackbar.make(nestedScrollView, "Nume de utilizator folosit", Snackbar.LENGTH_LONG).show();
        }
        return false;
    }
    private void emptyInputEditText(){
        textInputEditName.setText(null);
        textInputEditPassword.setText(null);
        textInputEditConfirmPassword.setText(null);
    }
}
