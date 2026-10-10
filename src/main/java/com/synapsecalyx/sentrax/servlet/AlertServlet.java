package com.synapsecalyx.sentrax.servlet;

import com.synapsecalyx.sentrax.dao.AlertDAO;
import com.synapsecalyx.sentrax.dao.AlertDAOImpl;
import com.synapsecalyx.sentrax.model.AppUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Lists unresolved alerts (GET) and lets an analyst resolve one (POST). */
@WebServlet("/alerts")
public class AlertServlet extends HttpServlet {

    private final AlertDAO alertDAO = new AlertDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("alerts", alertDAO.findUnresolved());
        req.getRequestDispatcher("/alerts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null || req.getSession().getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        long alertId = Long.parseLong(req.getParameter("alertId"));
        AppUser current = (AppUser) req.getSession().getAttribute("currentUser");
        alertDAO.resolve(alertId, current.getUsername());

        resp.sendRedirect(req.getContextPath() + "/alerts");
    }
}
