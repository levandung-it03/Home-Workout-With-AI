package com.restproject.backend.services.ThirdParty;

import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class CryptoService {
    @Value("${services.back-end.crypto.key}")
    private String base64Key;

    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    private static final byte[] IV = new byte[16];

    public String encrypt(String raw) throws ApplicationException {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] encrypted = cipher.doFinal(raw.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCodes.WEIRD_DECODED_CHARACTERS);
        }
    }

    public String decrypt(String encryptedText) throws ApplicationException {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decoded), CHARSET);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCodes.WEIRD_DECODED_CHARACTERS);
        }
    }
}
