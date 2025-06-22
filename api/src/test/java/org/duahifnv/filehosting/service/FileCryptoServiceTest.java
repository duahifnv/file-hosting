package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.exception.CryptoException;
import org.duahifnv.filehosting.model.CryptoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileCryptoServiceTest {
    private final FileCryptoService service = new FileCryptoService();
    private String originalText;
    private CryptoData cryptoData;

    @BeforeEach
    void setupInput() throws CryptoException {
        originalText = "Text to encrypt";
        var is = new ByteArrayInputStream(originalText.getBytes(StandardCharsets.UTF_8));

        cryptoData = service.encryptStream(is);
        assertNotNull(cryptoData);
    }

    @Test
    void encryptAndDecryptData_shouldBeEqualStrokes_withValidIvAndKey() throws Exception {
        // then: Проверка, что зашифрованный текст не совпадает с оригинальным
        assertNotNull(cryptoData);
        assertFalse(Arrays.equals(originalText.getBytes(StandardCharsets.UTF_8), cryptoData.bytes()));

        // then: Проверка на совпадение дешифрованного текста с оригинальным
        var decryptedBytes = service.decryptData(cryptoData);
        var decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void encryptAndDecryptData_shouldNotBeEqualStreams_withNotValidIv() throws Exception {
        // when: Подмена вектор инициализации
        var wrongIv = Arrays.copyOf(cryptoData.iv(), cryptoData.iv().length);
        wrongIv[0] ^= (byte) 0xFF;
        var wrongCryptoData = new CryptoData(cryptoData.bytes(), cryptoData.secretKey(), wrongIv);

        // then: Проверка на различие дешифрованного текста с оригинальным
        var decryptedBytes = service.decryptData(wrongCryptoData);
        var decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
        assertNotEquals(originalText, decryptedText);
    }

    @Test
    void encryptAndDecryptStream_shouldThrowException_withNotValidKey() throws Exception {
        // when: Подмена ключа
        var wrongKey = Arrays.copyOf(cryptoData.secretKey(), cryptoData.secretKey().length);
        wrongKey[0] ^= (byte) 0xFF;
        var wrongCryptoData = new CryptoData(cryptoData.bytes(), wrongKey, cryptoData.iv());

        // then: Проверка на выброс исключения методом дешифрования текста
        assertThrows(CryptoException.class, () -> service.decryptData(wrongCryptoData));
    }
}