package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * This record is a generic paginated response envelope.
 *
 * @param content       the items on the current page
 * @param page          zero based current page index
 * @param size          page size
 * @param totalElements total number of elements across all pages
 * @param totalPages    total number of pages
 * @param last          whether this is the last page or not
 */
@Schema(description = "Generic paginated response envelope")
public record PagedResult<T>(

        @Schema(description = "Items on the current page")
        List<T> content,

        @Schema(description = "Zero based current page index", example = "0")
        int page,

        @Schema(description = "Page size", example = "2")
        int size,

        @Schema(description = "Total number of elements across all pages", example = "55")
        long totalElements,

        @Schema(description = "Total number of pages", example = "8")
        int totalPages,

        @Schema(description = "Whether this is the last page or not", example = "false")
        boolean last
) {

    public static <T, R> PagedResult<R> from(Page<T> page, java.util.function.Function<T, R> itemMapper) {
        return new PagedResult<>(
                page.getContent().stream().map(itemMapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}