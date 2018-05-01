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

import java.util.ArrayList;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.model.Item;
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
    private int[] passwordIDs;
    private ArrayList<Item> arrayListOfItems;

    protected void onCreate(Bundle savedInstanceStates){
        super.onCreate(savedInstanceStates);
        setContentView(R.layout.activity_users);

        buttonNewPassword = findViewById(R.id.buttonNewPassword);
        listView = findViewById(R.id.User_ListView);
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        passwordIDs = databaseHelper.getPasswordIDs();
        arrayListOfItems = databaseHelper.getItems();
        ArrayList<String> strings = new ArrayList<>();
        for (Item i: arrayListOfItems) {
            strings.add(i.getUsername());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UsersActivity.this,
                android.R.layout.simple_list_item_1, strings);
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
                Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                startActivityForResult(intent,1);
                break;
                /*
                *   Maybe add extra features?
                *
                * */
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, UsersActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }

    private void checkDetails(){

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(UsersActivity.this, ItemDetailsActivity.class);
        intent.putExtra("ID", arrayListOfItems.get(i).getId());
        intent.putExtra("Password", arrayListOfItems.get(i).getDecryptedPassword());
        intent.putExtra("Username", arrayListOfItems.get(i).getUsername());
        intent.putExtra("Details", arrayListOfItems.get(i).getDescription());
        intent.putExtra("UsedFor", arrayListOfItems.get(i).getUsedFor());
        startActivityForResult(intent, 1);
    }
}
