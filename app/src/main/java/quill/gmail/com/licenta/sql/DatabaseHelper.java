package quill.gmail.com.licenta.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import quill.gmail.com.licenta.helper.BCrypt;
import quill.gmail.com.licenta.helper.Decryptor;
import quill.gmail.com.licenta.helper.InputValidation;
import quill.gmail.com.licenta.model.Item;
import quill.gmail.com.licenta.model.User;

/**
 * Created by Paul on 1/4/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_HASH = "user_hash";
    private static final String COLUMN_USER_SALT = "user_salt";


    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_PASSWORD_ID = "password_id";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSWORD_DESCRIPTION = "password_description";
    private static final String COLUMN_PASSWORD_SALT = "password_salt";
    private static final String COLUMN_PASSWORD_USEDFOR = "password_usedfor";
    private static final String COLUMN_PASSWORD_USERNAME = "password_username";
    private SQLiteDatabase database;

    private String CREATE_USER_TABLE = "CREATE TABLE "
            + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_HASH + " TEXT"
            + ")";

    public String CREATE_PASSWORD_TABLE = "CREATE TABLE "
            + TABLE_PASSWORDS + "("
            + COLUMN_PASSWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PASSWORD_SALT + " data BLOB,"
            + COLUMN_PASSWORD_USEDFOR + " TEXT,"
            + COLUMN_PASSWORD_USERNAME + " TEXT,"
            + COLUMN_PASSWORD_DESCRIPTION + " TEXT" +")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_PASSWORD_TABLE = "DROP TABLE IF EXISTS " + TABLE_PASSWORDS;
    public DatabaseHelper(Context context, String database_name){
        super(context, database_name, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PASSWORD_TABLE);
        database = db;
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
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_HASH, user.getHash());
        db.insert(TABLE_USER, null, values);
    }

    public void addPassword(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, item.getPassword());
        values.put(COLUMN_PASSWORD_DESCRIPTION, item.getDescription());
        values.put(COLUMN_PASSWORD_SALT, item.getSalt());
        values.put(COLUMN_PASSWORD_USEDFOR, item.getUsedFor());
        values.put(COLUMN_PASSWORD_USERNAME, item.getUsername());
        db.insert(TABLE_PASSWORDS,null,  values);
    }

    public void deletePassword(String name, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs[] = {id};
        String whereClause = "password_id=?";

        db.delete(TABLE_PASSWORDS, whereClause, whereArgs);
    }

    public boolean checkUser(String user, String password){
        String[] columns = new String[] {
                COLUMN_USER_NAME,
                COLUMN_USER_ID,
                COLUMN_USER_HASH
        };
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "user_name=?";
        String[] selectionArgs = new String[] {user};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null,
        null, null);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        if(cursorCount > 0){
            BCrypt bCrypt = new BCrypt();
            if(bCrypt.checkpw(password, cursor.getString(cursor.getColumnIndex(COLUMN_USER_HASH)))){
                cursor.close();
                db.close();
                return true;
            }
        }
        cursor.close();
        db.close();
        return false;
    }

    /*

        As putea returna obiecte de tip ? si popula array-ul cu alea

     */
    public List<String> getPasswordNames(){
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
    public int[] getPasswordIDs(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns  = new String[] {  COLUMN_PASSWORD_ID };
        int[] result = new int[100];
        int counter = 0;
        Cursor c = db.query(TABLE_PASSWORDS, columns,
                null, null, null, null, null);
        int ICM = c.getColumnIndex(COLUMN_PASSWORD_ID);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            result[counter] = c.getInt(ICM);
            counter++;
        }
        return result;
    }

    public ArrayList<Item> getItems(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns  = new String[] {  COLUMN_PASSWORD_ID, COLUMN_PASSWORD_SALT, COLUMN_PASSWORD,
                COLUMN_PASSWORD_USEDFOR, COLUMN_PASSWORD_DESCRIPTION, COLUMN_PASSWORD_USERNAME};
        ArrayList<Item> list = new ArrayList<>();

        Cursor c = db.query(TABLE_PASSWORDS, columns,
                null, null, null, null, null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            list.add(new Item(
                    c.getString(c.getColumnIndex(COLUMN_PASSWORD)),
                    c.getInt(c.getColumnIndex(COLUMN_PASSWORD_ID)),
                    c.getBlob(c.getColumnIndex(COLUMN_PASSWORD_SALT)),
                    c.getString(c.getColumnIndex(COLUMN_PASSWORD_USEDFOR)),
                    c.getString(c.getColumnIndex(COLUMN_PASSWORD_DESCRIPTION)),
                    c.getString(c.getColumnIndex(COLUMN_PASSWORD_USERNAME))));
        }
        return list;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
    public void exeSQL(String string){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_PASSWORD_TABLE);
        db.execSQL(string);
    }
}
