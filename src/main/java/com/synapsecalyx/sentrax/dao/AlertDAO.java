package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.model.Alert;

import java.util.List;

public interface AlertDAO extends GenericDAO<Alert, Long> {
    List<Alert> findUnresolved();
    void resolve(long alertId, String resolvedBy);
}
