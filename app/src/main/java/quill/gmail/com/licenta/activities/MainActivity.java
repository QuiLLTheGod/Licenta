package quill.gmail.com.licenta.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;

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

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initViews();
        initListeners();
        initObjects();
    }


    private void initViews(){
        nestedScrollView =  findViewById(R.id.nestedScrollView);

        textInputLayoutEmail =  findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputEditTextPassword =  findViewById(R.id.textInputEditTextPassword);
        textInputEditTextUser  = findViewById(R.id.textInputEditTextUser);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister =  findViewById(R.id.textViewLinkRegister);

        progressBar = findViewById(R.id.progressbar);
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
                new Task().execute();
                break;
            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }
    private void verifyFromSQLite(){
        databaseHelper = new DatabaseHelper(activity, textInputEditTextUser.getText().toString().trim());

       // if(!inputValidation.isinputEditTextFilled(textInputEditTextUser, textInputLayoutEmail, getString(R.string.error_email_exists)))
        //    return;

       // if(!inputValidation.isinputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password)))
        //    return;

        if(databaseHelper.checkUser(textInputEditTextUser.getText().toString().trim(),
                                    textInputEditTextPassword.getText().toString().trim())){
            User.NAME = textInputEditTextUser.getText().toString().trim();
            Intent accountsIntent = new Intent(activity, ItemsListActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextUser.getText().toString().trim());
            startActivity(accountsIntent);
        }
        else{
            inputValidation.hideKeyboardFrom(textInputEditTextUser);
            inputValidation.hideKeyboardFrom(textInputEditTextPassword);
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_username_password), Snackbar.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText(){
        textInputEditTextUser.setText(null);
        textInputEditTextPassword.setText(null);
    }
    public class Task  extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            verifyFromSQLite();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.INVISIBLE);
            emptyInputEditText();
            super.onPostExecute(aBoolean);
        }
    }
}
