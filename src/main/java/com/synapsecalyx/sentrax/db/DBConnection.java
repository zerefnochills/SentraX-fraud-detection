package com.synapsecalyx.sentrax.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Minimal JDBC connection helper. A new connection is opened per call
 * and closed by the DAO's try-with-resources block; this keeps the
 * example dependency-free (no external connection-pool library), which
 * is fine for a hackathon project. The README calls out swapping this
 * for a pool (HikariCP) as the first production hardening step.
 */
public final class DBConnection {

    private static final String CONFIG_FILE = "/db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBConnection.class.getResourceAsStream(CONFIG_FILE)) {
            Properties props = new Properties();
            if (in != null) {
                props.load(in);
            }
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/sentrax?useSSL=false&serverTimezone=UTC");
            user = props.getProperty("db.user", "root");
            password = props.getProperty("db.password", "");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Failed to initialize database configuration: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
