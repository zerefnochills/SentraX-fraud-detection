package com.synapsecalyx.sentrax.exception;

/**
 * Thrown when a transaction submitted to the system is missing required
 * fields or contains values that cannot be valid (e.g. a negative
 * amount). Checked, because callers (servlets) are expected to catch it
 * and show the user a clear message rather than let the request fail
 * silently.
 */
public class InvalidTransactionException extends Exception {

    public InvalidTransactionException(String message) {
        super(message);
    }

    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
