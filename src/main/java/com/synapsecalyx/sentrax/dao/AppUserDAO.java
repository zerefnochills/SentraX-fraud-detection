package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.model.AppUser;

import java.util.Optional;

public interface AppUserDAO extends GenericDAO<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
