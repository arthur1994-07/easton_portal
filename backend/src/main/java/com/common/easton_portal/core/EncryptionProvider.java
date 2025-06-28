package com.common.easton_portal.core;

import com.common.core.base.helper.StringHelper;

import java.util.Base64;

public interface EncryptionProvider {
    byte[] encrypt(byte[] data) throws Exception;
    byte[] decrypt(byte[] data) throws Exception;

    default String decryptFromBase64(String data) throws Exception {
        return StringHelper.fromBytes(decrypt(Base64.getDecoder().decode(data)));
    }

    default String encryptToBase64(String data) throws Exception {
        return Base64.getEncoder().encodeToString(encrypt(StringHelper.getBytes(data)));
    }
}
