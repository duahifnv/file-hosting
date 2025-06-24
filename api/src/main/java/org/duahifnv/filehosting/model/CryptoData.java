package org.duahifnv.filehosting.model;

public record CryptoData(byte[] bytes, byte[] secretKey, byte[] iv) {
}
