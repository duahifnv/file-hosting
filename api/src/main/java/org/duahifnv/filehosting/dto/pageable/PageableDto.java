package org.duahifnv.filehosting.dto.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageableDto {
    Integer page();
    Integer size();
    String sortParam();
    Sort.Direction sortDirection();

    default Pageable pageable() {
        var direction = sortDirection() != null ? sortDirection() : Sort.Direction.ASC;
        return PageRequest.of(page(), size(), direction, sortParam());
    }
}
