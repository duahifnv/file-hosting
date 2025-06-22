package org.duahifnv.filehosting.model.listener;

import org.duahifnv.filehosting.config.TestClockConfig;
import org.duahifnv.filehosting.model.FileMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileMetaEntityListenerTest {
    @InjectMocks
    private FileMetaEntityListener listener;

    @Spy
    private Clock clock = new TestClockConfig().clock();

    private Duration fileLifetime;

    @BeforeEach
    void setUp() {
        fileLifetime = Duration.of(1L, ChronoUnit.HOURS);
        listener.fileLifetime = fileLifetime;
    }

    @Test
    void setFileMeta_shouldSetMetadata_withNonExistingMetadata() {
        // given
        var fileMeta = spy(FileMeta.class);
        var dateTime = OffsetDateTime.ofInstant(clock.instant(), clock.getZone());

        // when
        listener.setFileMeta(fileMeta);

        // then
        assertThat(fileMeta.getCreatedAt()).isEqualTo(dateTime);
        assertThat(fileMeta.getExpiresAt()).isEqualTo(dateTime.plus(fileLifetime));
    }

    @Test
    void setFileMeta_shouldDoNothing_withExistingMetadata() {
        // given
        var fileMeta = spy(FileMeta.class);
        var dateTime = OffsetDateTime.ofInstant(clock.instant(), clock.getZone());

        when(fileMeta.getCreatedAt()).thenReturn(dateTime);
        when(fileMeta.getExpiresAt()).thenReturn(dateTime);

        // when
        listener.setFileMeta(fileMeta);

        // then
        verify(fileMeta, never()).setCreatedAt(any(OffsetDateTime.class));
        verify(fileMeta, never()).setExpiresAt(any(OffsetDateTime.class));
    }
}
