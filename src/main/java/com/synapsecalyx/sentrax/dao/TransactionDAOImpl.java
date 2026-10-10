package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.db.DBConnection;
import com.synapsecalyx.sentrax.model.RiskLevel;
import com.synapsecalyx.sentrax.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public Transaction save(Transaction t) {
        String sql = "INSERT INTO transaction "
                + "(customer_id, amount, currency, merchant, location, device_id, txn_time, status, risk_score, risk_level) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, t.getCustomerId());
            ps.setDouble(2, t.getAmount());
            ps.setString(3, t.getCurrency());
            ps.setString(4, t.getMerchant());
            ps.setString(5, t.getLocation());
            ps.setString(6, t.getDeviceId());
            ps.setTimestamp(7, Timestamp.valueOf(t.getTimestamp()));
            ps.setString(8, t.getStatus().name());
            ps.setDouble(9, t.getRiskScore());
            ps.setString(10, t.getRiskLevel().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getLong(1));
                }
            }
            return t;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction.", e);
        }
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM transaction WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load transaction " + id, e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        return query("SELECT * FROM transaction ORDER BY txn_time DESC", ps -> { });
    }

    @Override
    public List<Transaction> findRecentByCustomer(long customerId, int limit) {
        return query("SELECT * FROM transaction WHERE customer_id = ? ORDER BY txn_time DESC LIMIT ?", ps -> {
            ps.setLong(1, customerId);
            ps.setInt(2, limit);
        });
    }

    @Override
    public List<Transaction> findFlagged() {
        return query("SELECT * FROM transaction WHERE status = 'FLAGGED' ORDER BY txn_time DESC", ps -> { });
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete transaction " + id, e);
        }
    }

    @FunctionalInterface
    private interface PreparedStatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Transaction> query(String sql, PreparedStatementBinder binder) {
        List<Transaction> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query transactions.", e);
        }
        return results;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getLong("id"));
        t.setCustomerId(rs.getLong("customer_id"));
        t.setAmount(rs.getDouble("amount"));
        t.setCurrency(rs.getString("currency"));
        t.setMerchant(rs.getString("merchant"));
        t.setLocation(rs.getString("location"));
        t.setDeviceId(rs.getString("device_id"));
        t.setTimestamp(rs.getTimestamp("txn_time").toLocalDateTime());
        t.setStatus(Transaction.Status.valueOf(rs.getString("status")));
        t.setRiskScore(rs.getDouble("risk_score"));
        t.setRiskLevel(RiskLevel.valueOf(rs.getString("risk_level")));
        return t;
    }
}
