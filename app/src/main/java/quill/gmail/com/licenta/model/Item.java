package quill.gmail.com.licenta.model;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import quill.gmail.com.licenta.helper.Decryptor;

/**
 * Created by Paul on 3/4/2018.
 */

public class Item {

    private String data;
    private String password;
    private byte[] salt;
    private int id;
    private String decryptedPassword;

    public Item() {
    }
    public Item(String password, int id, byte[] salt){
        setSalt(salt);
        setPassword(password);
        setId(id);

    }

    public int getId(){return id;}
    public void setId(int id){ this.id = id; }

    public void setData(String data) {
        this.data = data;

    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getData() {
        return data;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
        try {
            Decryptor decryptor = new Decryptor();
            decryptedPassword = decryptor.decryptData(salt);
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

    public String getDecryptedPassword() {
        return decryptedPassword;
    }
}
