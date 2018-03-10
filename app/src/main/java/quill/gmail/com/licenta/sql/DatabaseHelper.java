package quill.gmail.com.licenta.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;

/**
 * Created by Paul on 1/4/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_PASSWORD_ID = "password_id";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSWORD_REF = "password_ref";
    private static final String COLUMN_PASSWORD_EMAIL = "password_email";
    private SQLiteDatabase database;

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String CREATE_PASSWORD_TABLE = "CREATE TABLE " + TABLE_PASSWORDS + "("
            + COLUMN_PASSWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PASSWORD_REF + " TEXT" +")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_PASSWORD_TABLE = "DROP TABLE IF EXISTS " + TABLE_PASSWORDS;
    public DatabaseHelper(Context context, String database_name){
        super(context, database_name, null, DATABASE_VERSION);

    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PASSWORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_PASSWORD_TABLE);
        onCreate(db);
    }
    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.NAME);
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        db.insert(TABLE_USER, null, values);
    }

    public void addPassword(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, item.getPassword());
        values.put(COLUMN_PASSWORD_REF, item.getData());
        db.insert(TABLE_PASSWORDS,null,  values);
    }

    public boolean checkUser(String user){
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null,
        null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if(cursorCount > 0){
            return true;
        }
        return false;
    }

    /*

        As putea returna obiecte de tip ? si popula array-ul cu alea

     */
    public List<String> getPasswords(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns  = new String[] {  COLUMN_PASSWORD };
        List<String> result = new ArrayList<String>();
        Cursor c = db.query(TABLE_PASSWORDS, columns,
                  null, null, null, null, null);
        int ICM = c.getColumnIndex(COLUMN_PASSWORD);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            result.add(c.getString(ICM));
        }
        return result;
    }
}
