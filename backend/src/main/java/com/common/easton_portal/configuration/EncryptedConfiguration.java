package com.common.easton_portal.configuration;

import com.common.easton_portal.core.EncryptedProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@Configuration
public class EncryptedConfiguration {
    private static final String K_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String K_KEY_METHOD = "PBKDF2WithHmacSHA256";
    private static final String K_KEY_ALGORITHM = "AES";
    private static final int K_KEY_ITERATION = 65536;
    private static final int K_KEY_LENGTH = 32;
    private static final int K_IV_LENGTH = 16;
    private static final int K_SALT_LENGTH = 16;

    private static final ThreadLocal<Random> K_RANDOM = ThreadLocal.withInitial(SecureRandom::new);

    @Value("${custom.encryptor.password}")
    private String defaultPassword;

    @Bean
    public EncryptedProvider encryptor() {
        return new EncryptedProvider() {
            @Override
            public byte[] encrypt(byte[] data) throws Exception {
                var key = generateKey(defaultPassword, generateSalt());
                var iv = generateIv();
                var cipherData = encryptProcess(data, key, iv);

                var buffer = ByteBuffer.allocate(K_IV_LENGTH + K_KEY_LENGTH + cipherData.length);
                buffer.put(iv.getIV());
                buffer.put(key.getEncoded());
                buffer.put(cipherData);
                return buffer.array();
            }

            @Override
            public byte[] decrypt(byte[] data) throws Exception {
                if (data.length <= K_KEY_LENGTH + K_IV_LENGTH) throw new IllegalArgumentException();

                var ivData = new byte[K_IV_LENGTH];
                var keyData = new byte[K_KEY_LENGTH];
                var cipherData = new byte[data.length - K_IV_LENGTH - K_KEY_LENGTH];

                System.arraycopy(data, 0, ivData, 0, ivData.length);
                System.arraycopy(data, K_IV_LENGTH, keyData, 0, keyData.length);
                System.arraycopy(data, K_IV_LENGTH + K_KEY_LENGTH, cipherData, 0, cipherData.length);

                var key = new SecretKeySpec(keyData, K_KEY_ALGORITHM);
                var iv = new IvParameterSpec(ivData);
                return decryptProcess(cipherData, key, iv);
            }
        };
    }


    private static SecretKey generateKey(String password, byte[] salt) throws
            NoSuchAlgorithmException, InvalidKeySpecException {
        var factory = SecretKeyFactory.getInstance(K_KEY_METHOD);
        var spec = new PBEKeySpec(password.toCharArray(), salt, K_KEY_ITERATION, K_KEY_LENGTH * 8);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), K_KEY_ALGORITHM);
    }

    private static byte[] generateSalt() {
        var salt = new byte[K_SALT_LENGTH];
        K_RANDOM.get().nextBytes(salt);
        return salt;
    }

    private static IvParameterSpec generateIv() {
        var iv = new byte[K_IV_LENGTH];
        K_RANDOM.get().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static byte[] encryptProcess(byte[] input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        var cipher = Cipher.getInstance(K_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input);
    }

    private static byte[] decryptProcess(byte[] cipherData, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        var cipher = Cipher.getInstance(K_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(cipherData);
    }


    //return Base64.getEncoder()
    //        .encodeToString(cipherText);
    //}
/*
    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }*/
}
