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
    private static final String COLUMN_USER_HASH = "user_hash";
    private static final String COLUMN_USER_SALT = "user_salt";


    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COLUMN_PASSWORD_ID = "password_id";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSWORD_REF = "password_ref";
    private static final String COLUMN_PASSWORD_SALT = "password_salt";
    private SQLiteDatabase database;

    private String CREATE_USER_TABLE = "CREATE TABLE "
            + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_HASH + " TEXT,"
            + COLUMN_USER_SALT + " data BLOB,"
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    public String CREATE_PASSWORD_TABLE = "CREATE TABLE "
            + TABLE_PASSWORDS + "("
            + COLUMN_PASSWORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_PASSWORD_SALT + " data BLOB,"
            + COLUMN_PASSWORD_REF + " TEXT" +")";

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
        values.put(COLUMN_USER_NAME, user.NAME);
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_HASH, user.getHash());
        values.put(COLUMN_USER_SALT, user.getSalt());
        db.insert(TABLE_USER, null, values);
    }

    public void addPassword(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, item.getPassword());
        values.put(COLUMN_PASSWORD_REF, item.getData());
        values.put(COLUMN_PASSWORD_SALT, item.getSalt());
        db.insert(TABLE_PASSWORDS,null,  values);
    }

    public void deletePassword(String name, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs[] = {id};
        String whereClause = "password_id=?";

        db.delete(TABLE_PASSWORDS, whereClause, whereArgs);
    }

    public boolean checkUser(String user, String password){
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_SALT,
                COLUMN_USER_HASH,
                COLUMN_USER_NAME
        };
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_NAME + "=?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null,
        null, null);
        int cursorCount = cursor.getCount();

        if(cursorCount > 0){
            try {
                Decryptor decryptor = new Decryptor();
                String decryptedSalt = decryptor.decryptData(cursor.getBlob(cursor.getColumnIndex(COLUMN_USER_SALT)));
                BCrypt bCrypt = new BCrypt();
                if(bCrypt.checkpw(password, cursor.getString(cursor.getColumnIndex(COLUMN_USER_HASH)))){
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
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
        String[] columns  = new String[] {  COLUMN_PASSWORD_ID, COLUMN_PASSWORD_SALT, COLUMN_PASSWORD};
        ArrayList<Item> list = new ArrayList<>();

        Cursor c = db.query(TABLE_PASSWORDS, columns,
                null, null, null, null, null);
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            list.add(new Item(
                    c.getString(c.getColumnIndex(COLUMN_PASSWORD)),
                    c.getInt(c.getColumnIndex(COLUMN_PASSWORD_ID)),
                    c.getBlob(c.getColumnIndex(COLUMN_PASSWORD_SALT))));
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
