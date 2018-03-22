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
    static final int SALT_LENGTH = 29;
    private Cipher cipher;
    private KeyGenerator keyGenerator;
    private SecretKey secretKey;
    private KeyStore keyStore;
    private String encryptedString;
    private byte[] IV;
    private byte[] decryptedData;
    private String decryptedString = null;

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
    public String getEncryptedString(String toEncrypt){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            IV = cipher.getIV();
            encryptedString = new String(cipher.doFinal(toEncrypt.getBytes("UTF-8")));
            return encryptedString + setIVToString();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "Error at encripting string";
    }
    public String getDecryptedString(){
        try {
             keyStore = KeyStore.getInstance("AndroidKeyStore");
             keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(User.NAME, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        final GCMParameterSpec spec = new GCMParameterSpec(128, IV);
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        try {
            decryptedData = cipher.doFinal(encryptedString.getBytes());
            return getIVFromString();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "Error at decrypting string";
    }
    private String setIVToString(){
        try {
            encryptedString += new String(IV, "UTF-8");
            return new String(IV, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Error at setting StringIV";
    }
    private String getIVFromString(){
        byte[]temp = encryptedString.getBytes();
        for(int i = 0; i < (encryptedString.length()-SALT_LENGTH);i++){
            decryptedString += temp[i];
        }
        return decryptedString;
    }
}
