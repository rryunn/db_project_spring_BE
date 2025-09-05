package com.acm.server.adapter.out.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class SimpleCryptoConverter implements AttributeConverter<String, String> {
    private static final String ALGO = "AES";
    private static final String KEY = "z0e4X3V5a3H7w4oYz+PQh0qTcsZaV+GwJyfQOoXepzQ="; // 32바이트 고정키 (예: 환경변수)

    private Cipher getCipher(int mode) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(KEY); // ✅ 디코딩해서 32바이트 확보
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGO);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(mode, keySpec);
        return cipher;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            if (attribute == null) return null;
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null) return null;
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(decrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption error", e);
        }
    }
}
