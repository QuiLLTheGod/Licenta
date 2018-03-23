package quill.gmail.com.licenta.helper;

import java.io.IOException;
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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import quill.gmail.com.licenta.model.User;

/**
 * Created by Paul on 3/23/2018.
 */

public class Decryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String ALIAS = User.NAME;
    private KeyStore keyStore;
    private byte[] encryptionIv;
    private byte[] data;

    public Decryptor() throws NoSuchAlgorithmException, KeyStoreException,
            IOException, CertificateException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException,
            NoSuchAlgorithmException, IOException, CertificateException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    public String decryptData(final byte[] encryptedData)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        split(encryptedData);
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

        return new String(cipher.doFinal(data), "UTF-8");
    }

    private void split(final byte[] encryptedData){
        final int dataLength = encryptedData[encryptedData.length-1];
        final int ivLength = encryptedData.length - dataLength - 1;
        data = new byte[dataLength];
        encryptionIv = new byte[ivLength];
        for(int i = 0;i<dataLength;i++){
            data[i] = encryptedData[i];
        }
        for(int j = dataLength;j<(dataLength+ivLength);j++){
            encryptionIv[j-dataLength] = encryptedData[j];
        }
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null)).getSecretKey();
    }
}