package priseceanpaul.gmail.com.licenta.helper;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import priseceanpaul.gmail.com.licenta.model.User;

/**
 * Created by Paul on 3/23/2018.
 */

public class Encryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String ALIAS = User.NAME;
    private KeyStore keyStore;
    private byte[] encryption;
    private byte[] iv;

    public Encryptor() {
    }

    public void encryptText(final String textToEncrypt)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        iv = cipher.getIV();
        encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
        concatenate();
    }

    @NonNull
    private SecretKey getSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null);
            return secretKeyEntry.getSecretKey();
        } catch (UnrecoverableEntryException e) {
            return generateSecretKey();
        } catch (KeyStoreException e) {
            return generateSecretKey();
        } catch (CertificateException e) {
            return generateSecretKey();
        } catch (IOException e) {
            return generateSecretKey();
        }
        catch (NullPointerException e){
            return generateSecretKey();
        }
    }
    private SecretKey generateSecretKey() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            keyGenerator.init(new KeyGenParameterSpec.Builder(ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
        }
        return keyGenerator.generateKey();
    }

    private void concatenate(){
        byte[] temp = new byte[encryption.length + iv.length + 1];
        for(int i = 0; i< encryption.length;i++){
            temp[i] = encryption[i];
        }
        for(int j = encryption.length; j<(encryption.length+iv.length);j++){
            temp[j] = iv[j-encryption.length];
        }
        temp[encryption.length + iv.length] = (byte) encryption.length;
        encryption = temp;
    }

    public byte[] getEncryption() {
        return encryption;
    }

    public byte[] getIv() {
        return iv;
    }
}
