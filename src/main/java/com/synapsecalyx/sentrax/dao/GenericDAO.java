package com.synapsecalyx.sentrax.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD contract implemented by every concrete DAO. A generic
 * interface here avoids repeating the same four method signatures in
 * {@code TransactionDAO}, {@code CustomerDAO}, {@code AlertDAO} and
 * {@code AppUserDAO}.
 *
 * @param <T>  the entity type
 * @param <ID> the entity's primary key type
 */
public interface GenericDAO<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
