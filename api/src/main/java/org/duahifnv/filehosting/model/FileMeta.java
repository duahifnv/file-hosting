package org.duahifnv.filehosting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.duahifnv.filehosting.model.listener.FileMetaEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_metadata")
@EntityListeners(FileMetaEntityListener.class)
public class FileMeta {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Size(max = 100)
    @Column(name = "content_type", length = 100)
    private String contentType;

    @NotNull
    @Column(name = "size", nullable = false)
    private Long size;

    @Size(max = 50)
    @NotNull
    @Column(name = "bucket", nullable = false, length = 50)
    private String bucket;

    @Size(max = 500)
    @NotNull
    @Column(name = "object_path", nullable = false, length = 500)
    private String objectPath;

    @NotNull
    @Column(name = "encryption_key", nullable = false)
    private byte[] encryptionKey;

    @NotNull
    @Column(name = "iv", nullable = false)
    private byte[] iv;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    public static FileMeta of(MultipartFile file, String bucketName, CryptoData cryptoData, User user) {
        var fileMeta = new FileMeta();
        fileMeta.setUser(user);
        fileMeta.setOriginalName(file.getOriginalFilename());
        fileMeta.setContentType(file.getContentType());
        fileMeta.setSize(file.getSize());
        fileMeta.setBucket(bucketName);
        fileMeta.setObjectPath("user-%s/%s.enc".formatted(user.getId(), UUID.randomUUID()));
        fileMeta.setEncryptionKey(cryptoData.secretKey());
        fileMeta.setIv(cryptoData.iv());
        return fileMeta;
    }
}