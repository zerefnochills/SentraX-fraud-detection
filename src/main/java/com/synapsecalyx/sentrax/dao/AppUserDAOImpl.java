package com.synapsecalyx.sentrax.dao;

import com.synapsecalyx.sentrax.db.DBConnection;
import com.synapsecalyx.sentrax.model.AppUser;
import com.synapsecalyx.sentrax.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppUserDAOImpl implements AppUserDAO {

    @Override
    public AppUser save(AppUser user) {
        String sql = "INSERT INTO app_user (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user.", e);
        }
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        String sql = "SELECT * FROM app_user WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user " + id, e);
        }
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        String sql = "SELECT * FROM app_user WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user " + username, e);
        }
    }

    @Override
    public List<AppUser> findAll() {
        String sql = "SELECT * FROM app_user ORDER BY username";
        List<AppUser> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load users.", e);
        }
        return users;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM app_user WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user " + id, e);
        }
    }

    private AppUser mapRow(ResultSet rs) throws SQLException {
        return new AppUser(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("full_name"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
