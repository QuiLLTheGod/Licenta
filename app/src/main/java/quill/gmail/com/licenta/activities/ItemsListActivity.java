package quill.gmail.com.licenta.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.fragments.PasswordsAdapter;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class ItemsListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ArrayList<Item> arrayListOfItems;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton buttonNewPassword;

    private void initViews(){
        recyclerView = findViewById(R.id.rv);
        buttonNewPassword = findViewById(R.id.buttonNewPassword);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getApplicationContext(), User.NAME);
        arrayListOfItems = databaseHelper.getItems();

        setContentView(R.layout.activity_items_list);
        initViews();

        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLayoutManager);
        PasswordsAdapter adapter = new PasswordsAdapter(this, arrayListOfItems);
        recyclerView.setAdapter(adapter);
        setListeners();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, ItemsListActivity.class);
            startActivity(refresh);
            this.finish();
        }
        else{
            Intent refresh = new Intent(this, ItemsListActivity.class);
            startActivity(refresh);
            this.finish();
        }
    }

    private void setListeners() {
        buttonNewPassword.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
}
