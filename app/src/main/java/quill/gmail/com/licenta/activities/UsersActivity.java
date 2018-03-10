package quill.gmail.com.licenta.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

/**
 * Created by Paul on 1/4/2018.
 */

public class UsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView textViewName;

    private Button buttonNewPassword;
    private DatabaseHelper databaseHelper;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceStates){
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activty_users);

        buttonNewPassword = (Button) findViewById(R.id.buttonNewPassword);
        listView =(ListView) findViewById(R.id.User_ListView);
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UsersActivity.this,
                android.R.layout.simple_list_item_1, databaseHelper.getPasswords());
        listView.setAdapter(arrayAdapter);
        setListeners();
    }
    private void setListeners(){
        buttonNewPassword.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonNewPassword:
                Intent intent = new Intent(getApplicationContext(), NewPasswordActivity.class);
                startActivity(intent);
                break;
                /*
                *   Maybe add extra features?
                *
                * */
            default:
                break;
        }
    }

    private void checkDetails(){

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(UsersActivity.this, ItemDetailsActivity.class);
        intent.putExtra("Nume", listView.getItemAtPosition(i).toString());
        startActivity(intent);
    }
}
