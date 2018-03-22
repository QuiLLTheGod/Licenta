package quill.gmail.com.licenta.helper;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import quill.gmail.com.licenta.model.User;

/**
 * Created by Paul on 3/20/2018.
 */

public class Cripter {
    private int SALT_LENGTH;
    private Cipher cipher;
    private KeyGenerator keyGenerator;
    private SecretKey secretKey;
    private KeyStore keyStore;
    private String encryptedString;
    private byte[] IV;
    private byte[] decryptedData;
    private String decryptedString;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Cripter() {
        try {
            keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(User.NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
        try {
            keyGenerator.init(keyGenParameterSpec);
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
    public void encryptString(String toEncrypt){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            IV = cipher.getIV();
            byte[] data= cipher.doFinal(toEncrypt.getBytes("UTF-8"));
            encryptedString = new String(data);
            setIVToString();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    public String getEncryptedString(String toEncrypt){
        SALT_LENGTH = toEncrypt.length();
        encryptString(toEncrypt);
        return encryptedString;
    }

    public void decryptString(){
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(User.NAME, null);
            GCMParameterSpec spec = new GCMParameterSpec(128, IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            getIVFromString();
            decryptedData = cipher.doFinal(decryptedString.getBytes());
            decryptedString = new String(decryptedData,"UTF-8");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public String getDecryptedString(){
        decryptString();
        return decryptedString;
    }
    private void setIVToString(){
        try {
            encryptedString += new String(IV, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void getIVFromString(){
        byte[]temp = encryptedString.getBytes();
        byte[]temp2 = new byte[SALT_LENGTH];
        for(int i = 0; i < SALT_LENGTH -1;i++){
            temp2[i] = temp[i];
        }
        try {
            decryptedString = new String(temp2, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
