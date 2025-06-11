package com.alephzero.alephzero.api.util.common.service;

import java.util.List;

/**
 * Generic CRUD service interface for basic operations on entities.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public interface CrudService<T, ID> {
    T findById(ID id);
    T findByIdReadOnly(ID id);
    List<T> findAll();
    List<T> findAllReadOnly();
    T save(T entity);
    void deleteById(ID id);
}
