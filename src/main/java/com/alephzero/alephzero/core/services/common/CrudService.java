package com.alephzero.alephzero.core.services.common;

import java.util.List;

/**
 * Generic CRUD service interface for basic operations on entities.
 *
 * @param <T> the type of the entity
 * @param <I> the type of the entity's identifier
 */
public interface CrudService<T, I> {
    T findById(I id);

    T findByIdReadOnly(I id);

    List<T> findAll();

    List<T> findAllReadOnly();

    T save(T entity);

    void deleteById(I id);
}
