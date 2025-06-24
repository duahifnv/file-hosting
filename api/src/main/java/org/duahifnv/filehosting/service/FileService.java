package org.duahifnv.filehosting.service;

import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.config.properties.MinioProperties;
import org.duahifnv.filehosting.dto.FileData;
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
    private final FileMetaService metaService;
    private final FileCryptoService cryptoService;
    private final MinioService minioService;
    private final MinioProperties minioProperties;

    public Optional<FileData> downloadFile(UUID id, User user) throws Exception {
        var metaOptional = metaService.findById(id, user);
        if (metaOptional.isEmpty()) {
            return Optional.empty();
        }
        var meta = metaOptional.get();
        byte[] encryptedBytes = minioService.downloadObject(meta);

        var cryptoData = new CryptoData(encryptedBytes, meta.getEncryptionKey(), meta.getIv());
        byte[] fileBytes = cryptoService.decryptData(cryptoData);

        return Optional.of(new FileData(meta, fileBytes));
    }

    @Transactional(rollbackFor = Exception.class)
    public UUID uploadFile(MultipartFile file, User user) throws Exception {
        CryptoData cryptoData = cryptoService.encryptStream(file.getInputStream());
        FileMeta savedMeta = metaService.save(FileMeta.of(
                file, minioProperties.getInitBucketName(), cryptoData, user)
        );

        minioService.uploadObject(savedMeta, cryptoData.bytes());
        return savedMeta.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeFile(UUID id, User user) throws Exception {
        Optional<FileMeta> metaOptional = metaService.findById(id, user);
        if (metaOptional.isEmpty()) {
            return false;
        }

        metaService.remove(metaOptional.get());
        minioService.removeObject(metaOptional.get());

        return true;
    }
}
