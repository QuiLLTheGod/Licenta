package quill.gmail.com.licenta.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class ItemDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonDelete;
    private Button buttonShowPassword;
    private Button buttonEdit;
    private TextView textViewUsedfor;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private EditText editTextDetails;


    private DatabaseHelper databaseHelper;
    private String id;
    private String password;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        extras = getIntent().getExtras();
        if(extras!=null){
            id = String.valueOf(extras.getInt("ID"));
            password =  extras.getString("Password");
        }
        initViews();
        initListeners();
    }

    private void initViews(){
        buttonShowPassword = findViewById(R.id.buttonIDAShowPassword);
        buttonDelete = findViewById(R.id.buttonItemDetailsDelete);
        buttonEdit = findViewById(R.id.buttonIDAEdit);

        editTextDetails = findViewById(R.id.editTextIDADetails);
        editTextUsername = findViewById(R.id.editTextIDAUsername);
        editTextPassword = findViewById(R.id.editTextIDAPassword);
        editTextPassword.setClickable(false);
        editTextPassword.setEnabled(false);
        editTextPassword.setText(password);
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
    }
    private void initListeners(){

        buttonDelete.setOnClickListener(this);
        buttonShowPassword.setOnClickListener(this);
    }

    private void showPassword(){
        editTextPassword.setTransformationMethod(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonItemDetailsDelete:
                createAlertDial();
                break;
            case R.id.buttonIDAShowPassword:
                showPassword();
                break;
        }
    }
    

    private void createAlertDial(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("");
        builder.setMessage("Are you sure you want to delete this password? This action cannot be undone!");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                postDataToSQL();
                setResult(RESULT_OK, null);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void postDataToSQL(){
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        databaseHelper.deletePassword(password, id);
    }
}
