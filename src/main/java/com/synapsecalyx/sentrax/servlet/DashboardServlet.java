package com.synapsecalyx.sentrax.servlet;

import com.synapsecalyx.sentrax.dao.AlertDAO;
import com.synapsecalyx.sentrax.dao.AlertDAOImpl;
import com.synapsecalyx.sentrax.dao.TransactionDAO;
import com.synapsecalyx.sentrax.dao.TransactionDAOImpl;
import com.synapsecalyx.sentrax.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAOImpl();
    private final AlertDAO alertDAO = new AlertDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Transaction> recent = transactionDAO.findAll();
        Collections.sort(recent); // Comparable -> highest risk first

        List<Transaction> flagged = transactionDAO.findFlagged();

        long safeCount = recent.stream().filter(t -> t.getRiskLevel().name().equals("SAFE")).count();
        long suspiciousCount = recent.stream().filter(t -> t.getRiskLevel().name().equals("SUSPICIOUS")).count();
        long fraudCount = recent.stream().filter(t -> t.getRiskLevel().name().equals("FRAUD")).count();

        req.setAttribute("recentTransactions", recent.size() > 25 ? recent.subList(0, 25) : recent);
        req.setAttribute("flaggedTransactions", flagged);
        req.setAttribute("unresolvedAlerts", alertDAO.findUnresolved());
        req.setAttribute("safeCount", safeCount);
        req.setAttribute("suspiciousCount", suspiciousCount);
        req.setAttribute("fraudCount", fraudCount);

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
