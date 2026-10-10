package com.synapsecalyx.sentrax.exception;

/**
 * Unchecked exception thrown when the risk scoring engine itself fails
 * (for example, a rule throws an unexpected runtime error). Kept
 * separate from {@link InvalidTransactionException} so callers can tell
 * "bad input" apart from "engine failure".
 */
public class FraudDetectionException extends RuntimeException {

    public FraudDetectionException(String message) {
        super(message);
    }

    public FraudDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
