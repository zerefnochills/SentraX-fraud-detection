package com.synapsecalyx.sentrax.engine;

import com.synapsecalyx.sentrax.dao.AlertDAO;
import com.synapsecalyx.sentrax.dao.CustomerDAO;
import com.synapsecalyx.sentrax.dao.TransactionDAO;
import com.synapsecalyx.sentrax.exception.InvalidTransactionException;
import com.synapsecalyx.sentrax.model.Alert;
import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.RiskLevel;
import com.synapsecalyx.sentrax.model.Transaction;
import com.synapsecalyx.sentrax.rules.FraudRule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application-level service tying the DAO layer to the
 * {@link RiskScoringEngine}. Servlets talk only to this class, not to
 * the engine or DAOs directly, keeping persistence and scoring
 * decoupled from the web layer.
 *
 * <p>Alert persistence is dispatched to a small background thread pool
 * so writing alert rows to the database never slows down the
 * analyst-facing request/response cycle. Each submitted task opens and
 * closes its own JDBC connection, so there is no shared mutable state
 * between threads — a simple, safe demonstration of multithreading in
 * a servlet environment.</p>
 */
public class FraudDetectionService {

    private static final Logger LOGGER = Logger.getLogger(FraudDetectionService.class.getName());

    private final TransactionDAO transactionDAO;
    private final CustomerDAO customerDAO;
    private final AlertDAO alertDAO;
    private final RiskScoringEngine engine;
    private final ExecutorService alertExecutor;

    public FraudDetectionService(TransactionDAO transactionDAO, CustomerDAO customerDAO, AlertDAO alertDAO) {
        this.transactionDAO = transactionDAO;
        this.customerDAO = customerDAO;
        this.alertDAO = alertDAO;
        this.engine = new RiskScoringEngine();
        this.alertExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Validates, scores, saves and (asynchronously) raises alerts for a
     * new transaction. Returns the saved transaction, now carrying its
     * risk score, level and status.
     */
    public Transaction processTransaction(Transaction transaction) throws InvalidTransactionException {
        validate(transaction);

        Customer customer = customerDAO.findById(transaction.getCustomerId())
                .orElseThrow(() -> new InvalidTransactionException(
                        "No customer found with id " + transaction.getCustomerId()));

        List<Transaction> history = transactionDAO.findRecentByCustomer(customer.getId(), 20);

        RiskScoringEngine.ScoringResult result = engine.score(transaction, customer, history);

        transaction.setRiskScore(result.getScore());
        transaction.setRiskLevel(result.getLevel());
        transaction.setStatus(result.getLevel() == RiskLevel.FRAUD
                ? Transaction.Status.FLAGGED
                : Transaction.Status.APPROVED);

        Transaction saved = transactionDAO.save(transaction);

        if (!result.getTriggeredRules().isEmpty()) {
            raiseAlertsAsync(saved, result.getTriggeredRules());
        }

        return saved;
    }

    private void raiseAlertsAsync(Transaction transaction, List<FraudRule> triggeredRules) {
        alertExecutor.submit(() -> {
            for (FraudRule rule : triggeredRules) {
                try {
                    Alert alert = new Alert(transaction.getId(), rule.getName(), rule.describeViolation(transaction));
                    alertDAO.save(alert);
                } catch (Exception ex) {
                    // A failure to persist one alert must not stop the others.
                    LOGGER.log(Level.WARNING, "Failed to save alert for rule " + rule.getName(), ex);
                }
            }
        });
    }

    private void validate(Transaction transaction) throws InvalidTransactionException {
        if (transaction == null) {
            throw new InvalidTransactionException("Transaction must not be null.");
        }
        if (transaction.getAmount() <= 0) {
            throw new InvalidTransactionException("Transaction amount must be positive.");
        }
        if (transaction.getCustomerId() <= 0) {
            throw new InvalidTransactionException("Transaction must reference a valid customer.");
        }
        if (transaction.getCurrency() == null || transaction.getCurrency().isEmpty()) {
            throw new InvalidTransactionException("Transaction currency is required.");
        }
    }

    public void shutdown() {
        alertExecutor.shutdown();
    }
}
