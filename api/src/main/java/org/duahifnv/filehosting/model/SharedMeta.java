package org.duahifnv.filehosting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.duahifnv.filehosting.model.listener.SharedMetaEntityListener;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "shared_files")
@EntityListeners(SharedMetaEntityListener.class)
public class SharedMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "metadata_id", nullable = false)
    private FileMeta metadata;

    @NotNull
    @Column(name = "share_mode", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ShareMode shareMode;

    @NotNull
    @Column(name = "shared_at", nullable = false)
    private OffsetDateTime sharedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @ManyToMany
    @JoinTable(name = "shared_files_users",
            joinColumns = @JoinColumn(name = "shared_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> sharedUsers;

    @Transient
    private Duration sharedDuration;

    @Transient
    private boolean isExpired = false;

    public static SharedMeta of(FileMeta fileMeta, List<User> sharedUsers, Duration sharedDuration) {
        SharedMeta sharedMeta = new SharedMeta();
        sharedMeta.setMetadata(fileMeta);
        sharedMeta.setSharedUsers(sharedUsers);
        sharedMeta.setSharedDuration(sharedDuration);

        ShareMode shareMode = (sharedUsers != null && !sharedUsers.isEmpty()) ? ShareMode.SELECTIVE : ShareMode.LINKED;
        sharedMeta.setShareMode(shareMode);
        return sharedMeta;
    }
}