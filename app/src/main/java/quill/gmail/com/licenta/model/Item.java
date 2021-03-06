package quill.gmail.com.licenta.model;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import quill.gmail.com.licenta.helper.Decryptor;
import quill.gmail.com.licenta.helper.Encryptor;

/**
 * Created by Paul on 3/4/2018.
 */

public class Item {

    private String description;
    private String username;
    private String password;
    private String usedFor;
    private byte[] salt;
    private int id;
    private int imageID;

    public Item() {
        description = "";
        username = "";
        password = "";
        usedFor = "";
    }
    public Item(String password, int id, byte[] salt, String usedFor, String description, String username, int imageID){
        setSalt(salt);
        setPassword(password);
        setId(id);
        setDescription(description);
        setUsedFor(usedFor);
        setUsername(username);
        setImageID(imageID);
    }
    public Item(String[] strings){
        Encryptor encryptor = new Encryptor();
        this.username = strings[0];
        try {
            encryptor.encryptText(strings[1]);
            this.salt = encryptor.getEncryption();
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchProviderException |
                InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | IOException |
                BadPaddingException | SignatureException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        this.usedFor = strings[2];
        this.description = strings[3];
    }

    public int getId(){return id;}
    public void setId(int id){ this.id = id; }

    public void setDescription(String data) {
        this.description = data;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getDecryptedPassword() {
        String decryptedPassword = "";
        try {
            Decryptor decryptor = new Decryptor();
            decryptedPassword = decryptor.decryptData(salt);
        } catch (NoSuchAlgorithmException | KeyStoreException
                | CertificateException | UnrecoverableEntryException |
                InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException |
                BadPaddingException | NoSuchProviderException | IOException e) {
            e.printStackTrace();
        }
        return decryptedPassword;
    }

    public String[] getAll(){
        return new String[]{
                this.username,
                this.getDecryptedPassword(),
                this.usedFor,
                this.description,
                String.valueOf(this.imageID)
        };
    }

    public String getUsedFor() {
        return usedFor;
    }

    public void setUsedFor(String usedFor) {
        this.usedFor = usedFor;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
