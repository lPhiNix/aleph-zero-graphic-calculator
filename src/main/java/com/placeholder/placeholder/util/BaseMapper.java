package com.placeholder.placeholder.util;

/**
 * Base interface for DTO<-->ENTITY mappers
 * @param <E> JPA Entity
 * @param <D> Base DTO
 */
public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
}
