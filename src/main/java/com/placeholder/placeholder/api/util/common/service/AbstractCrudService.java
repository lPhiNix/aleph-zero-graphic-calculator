package com.placeholder.placeholder.api.util.common.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * AbstractCrudService provides a base implementation for CRUD operations
 * using a JpaRepository. It handles common operations such as finding,
 * saving, and deleting entities.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 * @param <R>  the concrete the JpaRepository for the entity
 */
public abstract class AbstractCrudService<T, ID extends Serializable, R extends JpaRepository<T, ID>> implements CrudService<T, ID> {
    protected R repository;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public AbstractCrudService(R repository) {
        this.repository = repository;
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such entity %s", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public T findByIdReadOnly(ID id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("No such entity %s", id)));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAllReadOnly() {
        return repository.findAll();
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}