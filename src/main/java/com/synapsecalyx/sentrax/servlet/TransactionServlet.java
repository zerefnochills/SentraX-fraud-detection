package com.synapsecalyx.sentrax.servlet;

import com.synapsecalyx.sentrax.dao.AlertDAO;
import com.synapsecalyx.sentrax.dao.AlertDAOImpl;
import com.synapsecalyx.sentrax.dao.CustomerDAO;
import com.synapsecalyx.sentrax.dao.CustomerDAOImpl;
import com.synapsecalyx.sentrax.dao.TransactionDAO;
import com.synapsecalyx.sentrax.dao.TransactionDAOImpl;
import com.synapsecalyx.sentrax.engine.FraudDetectionService;
import com.synapsecalyx.sentrax.exception.InvalidTransactionException;
import com.synapsecalyx.sentrax.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles the "submit a new transaction" form and runs it through the
 * fraud detection pipeline. GET shows the form; POST evaluates it.
 */
@WebServlet("/transaction/new")
public class TransactionServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final AlertDAO alertDAO = new AlertDAOImpl();
    private final FraudDetectionService service =
            new FraudDetectionService(transactionDAO, customerDAO, alertDAO);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireLogin(req, resp)) {
            return;
        }
        req.setAttribute("customers", customerDAO.findAll());
        req.getRequestDispatcher("/newTransaction.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!requireLogin(req, resp)) {
            return;
        }

        try {
            Transaction transaction = buildFromRequest(req);
            Transaction scored = service.processTransaction(transaction);

            req.setAttribute("scoredTransaction", scored);
            req.getRequestDispatcher("/transactionResult.jsp").forward(req, resp);

        } catch (InvalidTransactionException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("customers", customerDAO.findAll());
            req.getRequestDispatcher("/newTransaction.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Amount and customer must be valid numbers.");
            req.setAttribute("customers", customerDAO.findAll());
            req.getRequestDispatcher("/newTransaction.jsp").forward(req, resp);
        }
    }

    private Transaction buildFromRequest(HttpServletRequest req) {
        long customerId = Long.parseLong(req.getParameter("customerId"));
        double amount = Double.parseDouble(req.getParameter("amount"));
        String currency = req.getParameter("currency");
        String merchant = req.getParameter("merchant");
        String location = req.getParameter("location");
        String deviceId = req.getParameter("deviceId");

        return new Transaction(customerId, amount, currency, merchant, location, deviceId, LocalDateTime.now());
    }

    private boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
