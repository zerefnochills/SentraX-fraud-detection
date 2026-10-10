package com.synapsecalyx.sentrax.engine;

import com.synapsecalyx.sentrax.model.Customer;
import com.synapsecalyx.sentrax.model.RiskLevel;
import com.synapsecalyx.sentrax.model.Transaction;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link RiskScoringEngine}. These run against plain
 * Java objects only — no database required — since the DAO layer is
 * intentionally kept out of the scoring engine's dependencies.
 */
public class RiskScoringEngineTest {

    private final RiskScoringEngine engine = new RiskScoringEngine();

    @Test
    public void normalTransactionIsScoredSafe() {
        Customer customer = new Customer(1, "Test User", "t@example.com", "Noida", "DEV-1");
        List<Transaction> history = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            history.add(new Transaction(1, 500, "INR", "Local Store", "Noida", "DEV-1",
                    LocalDateTime.now().minusDays(i + 1)));
        }

        Transaction current = new Transaction(1, 600, "INR", "Local Store", "Noida", "DEV-1", LocalDateTime.now());

        RiskScoringEngine.ScoringResult result = engine.score(current, customer, history);

        assertEquals(RiskLevel.SAFE, result.getLevel());
    }

    @Test
    public void largeUnusualPurchaseFromNewLocationIsFlagged() {
        Customer customer = new Customer(2, "Test User 2", "t2@example.com", "Noida", "DEV-2");
        List<Transaction> history = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            history.add(new Transaction(2, 400, "INR", "Local Store", "Noida", "DEV-2",
                    LocalDateTime.now().minusDays(i + 1)));
        }

        Transaction current = new Transaction(2, 150000, "INR", "Unknown Merchant", "Unknown City",
                "DEV-999", LocalDateTime.now().withHour(2));

        RiskScoringEngine.ScoringResult result = engine.score(current, customer, history);

        assertTrue("Expected an elevated risk score for a large, unusual transaction", result.getScore() > 50);
    }

    @Test
    public void rapidBurstOfTransactionsRaisesVelocityScore() {
        Customer customer = new Customer(3, "Test User 3", "t3@example.com", "Delhi", "DEV-3");
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> history = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            history.add(new Transaction(3, 400, "INR", "Store", "Delhi", "DEV-3", now.minusMinutes(i * 2)));
        }

        Transaction current = new Transaction(3, 400, "INR", "Store", "Delhi", "DEV-3", now);

        RiskScoringEngine.ScoringResult result = engine.score(current, customer, history);

        assertTrue("Expected the velocity rule to raise the risk score", result.getScore() > 0);
    }
}
