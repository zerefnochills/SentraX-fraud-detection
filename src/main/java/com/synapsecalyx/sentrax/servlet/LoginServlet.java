package com.synapsecalyx.sentrax.servlet;

import com.synapsecalyx.sentrax.dao.AppUserDAO;
import com.synapsecalyx.sentrax.dao.AppUserDAOImpl;
import com.synapsecalyx.sentrax.model.AppUser;
import com.synapsecalyx.sentrax.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AppUserDAO appUserDAO = new AppUserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<AppUser> userOpt = appUserDAO.findByUsername(username);

        if (userOpt.isPresent() && PasswordUtil.matches(password, userOpt.get().getPasswordHash())) {
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", userOpt.get());
            session.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Invalid username or password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
