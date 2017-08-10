package org.jocean.idiom;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public enum AESUtil {
    ;
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    
    public static byte[] encrypt(final String src, final String key, final String iv) {
        try {
            final Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, makeKey(key), makeIv(iv));
            return cipher.doFinal(src.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String decrypt(final byte[] encrypted, final String key, final String iv) {
        try {
            final Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, makeKey(key), makeIv(iv));
            return new String(cipher.doFinal(encrypted), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static AlgorithmParameterSpec makeIv(final String iv) throws UnsupportedEncodingException {
        return new IvParameterSpec(iv.getBytes("UTF-8"));
    }
    
    static Key makeKey(final String passwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final KeyGenerator kgen = KeyGenerator.getInstance("AES");
        final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(
            MessageDigest.getInstance("SHA-256").digest(passwd.getBytes("UTF-8")));
        kgen.init(128, secureRandom);
        final SecretKey secretKey = kgen.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
    
    public static void main(String[] args) {
        final String ENCRYPTION_KEY = "RwcmlVpg";
        final String ENCRYPTION_IV = "4e5Wa71fYoT7MFEX";
        
        final String text = "hello, world!";
        System.out.println(
            decrypt(encrypt(text, ENCRYPTION_KEY, ENCRYPTION_IV), ENCRYPTION_KEY, ENCRYPTION_IV));
    }
}
