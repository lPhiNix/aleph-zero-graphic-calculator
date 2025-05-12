package com.placeholder.placeholder.api.util.common.messages.dto.content;

/**
 * DTO model for creating a response with paged results
 * @param totalItems the total amount of items collected
 * @param totalPages the total amount of pages
 * @param currentPage current page
 * @param pageSize page size
 * @param hasNext has a next page
 * @param hasPrevious has a previous page
 * @param sort sort type
 * @param data embedded data
 * @param <T> type of the data
 */
public record PagedResponse<T>(
        int totalItems,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious,
        String sort,
        T data
) implements MessageContent {
    public static <T> PagedResponse<T> of(
            int totalItems,
            int currentPage,
            int pageSize,
            String sort,
            T data
    ) {
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new PagedResponse<>(
                totalItems,
                totalPages,
                currentPage,
                pageSize,
                currentPage < totalPages,
                currentPage > 1,
                sort,
                data
        );
    }
}