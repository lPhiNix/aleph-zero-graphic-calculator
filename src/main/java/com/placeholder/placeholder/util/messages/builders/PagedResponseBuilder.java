package com.placeholder.placeholder.util.messages.builders;

import com.placeholder.placeholder.util.messages.dto.PagedResponse;

/**
 * Builder class for creating {@link PagedResponse} instances.
 *
 * @param <T> the type of the data being paginated
 */
public class PagedResponseBuilder<T> {
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sort;
    private T data;

    private PagedResponseBuilder() {
    }

    /**
     * Creates a new instance of {@code PagedResponseBuilder}.
     *
     * @param <T> the type of the data being paginated
     * @return a new builder instance
     */
    public static <T> PagedResponseBuilder<T> builder() {
        return new PagedResponseBuilder<>();
    }

    /**
     * Sets the total number of items available.
     *
     * @param totalItems total item count
     * @return this builder instance
     */
    public PagedResponseBuilder<T> totalItems(int totalItems) {
        this.totalItems = totalItems;
        return this;
    }

    /**
     * Sets the total number of pages.
     *
     * @param totalPages total page count
     * @return this builder instance
     */
    public PagedResponseBuilder<T> totalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    /**
     * Sets the current page number.
     *
     * @param currentPage current page number
     * @return this builder instance
     */
    public PagedResponseBuilder<T> currentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    /**
     * Sets the number of items per page.
     *
     * @param pageSize page size
     * @return this builder instance
     */
    public PagedResponseBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Sets whether there is a next page.
     *
     * @param hasNext {@code true} if there is a next page
     * @return this builder instance
     */
    public PagedResponseBuilder<T> hasNext(boolean hasNext) {
        this.hasNext = hasNext;
        return this;
    }

    /**
     * Sets whether there is a previous page.
     *
     * @param hasPrevious {@code true} if there is a previous page
     * @return this builder instance
     */
    public PagedResponseBuilder<T> hasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
        return this;
    }

    /**
     * Sets the sorting criteria.
     *
     * @param sort sorting field or criteria
     * @return this builder instance
     */
    public PagedResponseBuilder<T> sort(String sort) {
        this.sort = sort;
        return this;
    }

    /**
     * Sets the paginated data.
     *
     * @param data the data object
     * @return this builder instance
     */
    public PagedResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * Builds and returns the {@link PagedResponse} instance.
     *
     * @return a new {@code PagedResponse} object
     */
    public PagedResponse<T> build() {
        return new PagedResponse<>(
                totalItems,
                totalPages,
                currentPage,
                pageSize,
                hasNext,
                hasPrevious,
                sort,
                data
        );
    }
}