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
        var page = page() != null ? page() : 0;
        var size = size() != null ? size() : 5;
        Sort sort = sortDirection() == null || sortParam() == null ? Sort.unsorted() : Sort.by(sortDirection(), sortParam());
        return PageRequest.of(page, size, sort);
    }
}
