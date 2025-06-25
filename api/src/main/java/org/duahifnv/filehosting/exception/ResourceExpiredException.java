package org.duahifnv.filehosting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

public class ResourceExpiredException extends ResponseStatusException {
    public ResourceExpiredException(OffsetDateTime expiredDateTime) {
        super(HttpStatus.GONE, "Искомый ресурс истек в " + expiredDateTime);
    }
}
