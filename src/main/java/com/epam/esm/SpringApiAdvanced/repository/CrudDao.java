package com.epam.esm.SpringApiAdvanced.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Optional;

public interface CrudDao<T, ID extends Serializable> {
    void save(T entity);

    Optional<T> findById(ID id);

    Page<T> findAll(Pageable pageable);

    void update(ID id, T entity);

    void deleteById(ID id);

}
