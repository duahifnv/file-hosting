package org.duahifnv.filehosting.dto.pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;

public record FilePageableDto(@Min(value = 0, message = "Номер страницы от 0")
                              @Schema(description = "Номер страницы", minimum = "0", example = "0")
                              Integer page,
                              @Min(value = 1, message = "Размер страницы от 1")
                              @Schema(description = "Размер страницы", minimum = "1", example = "10")
                              Integer size,
                              @Schema(description = "Параметр для сортировки элементов")
                              FileSort sort,
                              @Schema(description = "Направление сортировки элементов", allowableValues = {"ASC", "DESC"})
                              Sort.Direction sortDirection)
        implements PageableDto {
    public enum FileSort { originalName, contentType, originalSize, createdAt, expiresAt }

    @Override
    public String sortParam() {
        return sort != null ? sort.name() : null;
    }
}
