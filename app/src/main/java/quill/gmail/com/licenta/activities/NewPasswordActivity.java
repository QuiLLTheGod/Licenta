package quill.gmail.com.licenta.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonConfirm;
    private Button buttonCancel;
    private EditText editTextInsertPass;
    private DatabaseHelper databaseHelper;
    private Item item;

    private void initViews(){

        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        editTextInsertPass = (EditText) findViewById(R.id.editTextInsertPass);
    }

    private void initListeners(){
        buttonConfirm.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
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
                finish();
                break;

            case R.id.buttonConfirm:
                postDataToSQL();
                break;
        }
    }

    private void postDataToSQL() {
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);

        item.setData("");
        item.setPassword(editTextInsertPass.getText().toString().trim());

        databaseHelper.addPassword(item);
    }
}
