package quill.gmail.com.licenta.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import quill.gmail.com.licenta.R;
import quill.gmail.com.licenta.model.User;
import quill.gmail.com.licenta.sql.DatabaseHelper;

public class ItemDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonDelete;
    private DatabaseHelper databaseHelper;
    private String id;
    private String name;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        extras = getIntent().getExtras();
        if(extras!=null){
            id = String.valueOf(extras.getInt("ID"));
            name =  extras.getString("Nume");
        }
        initViews();
        initListeners();
    }

    private void initViews(){
        buttonDelete = findViewById(R.id.buttonItemDetailsDelete);

    }
    private void initListeners(){
        buttonDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonItemDetailsDelete:
                createAlertDial();
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
        databaseHelper.deletePassword(name, id);
    }
}
