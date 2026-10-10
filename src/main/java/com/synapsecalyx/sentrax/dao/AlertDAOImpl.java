package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.db.DBConnection;
import com.synapsecalyx.sentrax.model.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlertDAOImpl implements AlertDAO {

    @Override
    public Alert save(Alert alert) {
        String sql = "INSERT INTO alert (transaction_id, rule_name, message, created_at, resolved) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, alert.getTransactionId());
            ps.setString(2, alert.getRuleName());
            ps.setString(3, alert.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(alert.getCreatedAt()));
            ps.setBoolean(5, alert.isResolved());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    alert.setId(keys.getLong(1));
                }
            }
            return alert;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save alert.", e);
        }
    }

    @Override
    public Optional<Alert> findById(Long id) {
        String sql = "SELECT * FROM alert WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load alert " + id, e);
        }
    }

    @Override
    public List<Alert> findAll() {
        return query("SELECT * FROM alert ORDER BY created_at DESC");
    }

    @Override
    public List<Alert> findUnresolved() {
        return query("SELECT * FROM alert WHERE resolved = FALSE ORDER BY created_at DESC");
    }

    @Override
    public void resolve(long alertId, String resolvedBy) {
        String sql = "UPDATE alert SET resolved = TRUE, resolved_by = ?, resolved_at = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resolvedBy);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, alertId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to resolve alert " + alertId, e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM alert WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete alert " + id, e);
        }
    }

    private List<Alert> query(String sql) {
        List<Alert> alerts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alerts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query alerts.", e);
        }
        return alerts;
    }

    private Alert mapRow(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setId(rs.getLong("id"));
        alert.setTransactionId(rs.getLong("transaction_id"));
        alert.setRuleName(rs.getString("rule_name"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        alert.setResolved(rs.getBoolean("resolved"));
        alert.setResolvedBy(rs.getString("resolved_by"));
        Timestamp resolvedAt = rs.getTimestamp("resolved_at");
        if (resolvedAt != null) {
            alert.setResolvedAt(resolvedAt.toLocalDateTime());
        }
        return alert;
    }
}
