package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.db.DBConnection;
import com.synapsecalyx.sentrax.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customer (full_name, email, home_location, registered_device_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getHomeLocation());
            ps.setString(4, customer.getRegisteredDeviceId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setId(keys.getLong(1));
                }
            }
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save customer.", e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customer WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customer " + id, e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customer ORDER BY full_name";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customers.", e);
        }
        return customers;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer " + id, e);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("home_location"),
                rs.getString("registered_device_id")
        );
    }
}
