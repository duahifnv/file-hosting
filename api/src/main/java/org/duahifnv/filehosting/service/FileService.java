package org.duahifnv.filehosting.service;

import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.model.CryptoData;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private static final String UPLOADS_BUCKET_NAME = "user-uploads";

    private final FileMetaService metaService;
    private final FileCryptoService cryptoService;
    private final MinioService minioService;

    public Optional<byte[]> downloadFile(UUID id, User user) throws Exception {
        var metaOptional = metaService.findById(id, user);
        if (metaOptional.isEmpty()) {
            return Optional.empty();
        }
        var meta = metaOptional.get();

        byte[] fileBytes = minioService.downloadObject(meta);
        var cryptoData = new CryptoData(fileBytes, meta.getEncryptionKey(), meta.getIv());
        return Optional.of(cryptoService.decryptData(cryptoData));
    }

    @Transactional
    public void uploadFile(MultipartFile file, User user) throws Exception {
        CryptoData cryptoData = cryptoService.encryptStream(file.getInputStream());
        FileMeta fileMeta = FileMeta.of(file, UPLOADS_BUCKET_NAME, cryptoData, user);

        metaService.save(fileMeta);
        minioService.uploadObject(fileMeta, cryptoData.bytes());
    }

    @Transactional
    public boolean removeFile(UUID id, User user) throws Exception {
        Optional<FileMeta> metaOptional = metaService.findById(id, user);
        if (metaOptional.isEmpty()) {
            return false;
        }
        minioService.removeObject(metaOptional.get());
        metaService.remove(metaOptional.get());
        return true;
    }
}
