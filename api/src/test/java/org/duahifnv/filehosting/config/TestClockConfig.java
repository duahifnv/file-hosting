package org.duahifnv.filehosting.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TestClockConfig {
    private final Instant fixedInstant = Instant.parse("2025-06-01T12:00:00Z");

    @Bean
    public Clock clock() {
        return Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    }
}
