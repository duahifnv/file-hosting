package org.duahifnv.filehosting.service;

import com.google.common.io.ByteStreams;
import org.duahifnv.filehosting.exception.CryptoException;
import org.duahifnv.filehosting.model.CryptoData;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;

@Service
public class FileCryptoService {

    public CryptoData encryptStream(InputStream input) throws CryptoException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            ByteArrayOutputStream inputByteStream = new ByteArrayOutputStream();
            try (CipherOutputStream output = new CipherOutputStream(inputByteStream, cipher)) {
                ByteStreams.copy(input, output);
            }

            return new CryptoData(inputByteStream.toByteArray(), secretKey.getEncoded(), iv);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public byte[] decryptData(CryptoData cryptoData) throws CryptoException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(cryptoData.secretKey(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(cryptoData.iv());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            ByteArrayOutputStream outputByteStream = new ByteArrayOutputStream();

            var decryptInput = new ByteArrayInputStream(cryptoData.bytes());
            try (CipherInputStream cipherInput = new CipherInputStream(decryptInput, cipher)) {
                ByteStreams.copy(cipherInput, outputByteStream);
            }

            return outputByteStream.toByteArray();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
