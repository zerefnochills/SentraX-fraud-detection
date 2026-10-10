package com.synapsecalyx.sentrax.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Small SHA-256 password hashing helper. Good enough to avoid storing
 * plain-text passwords in a student project; a production system would
 * use bcrypt/Argon2 with a per-user salt instead.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to hash password.", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return hash(rawPassword).equals(storedHash);
    }
}
