package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.UserDAO;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.security.CustomCallbackHandler;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("hr_management_pu");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("Login attempt - Email: " + email);

        EntityManager em = emf.createEntityManager();
        UserDAO userDAO = new UserDAO(em);

        try {
            User user = userDAO.findByEmail(email);
            System.out.println("User found: " + (user != null));

            if (user != null) {
                System.out.println("Stored password hash: " + user.getPassword());

                boolean passwordMatches = BCrypt.checkpw(password, user.getPassword());
                System.out.println("Password matches: " + passwordMatches);

                if (passwordMatches) {
                    request.getSession().setAttribute("user", user);
                    response.sendRedirect(request.getContextPath() + "/employee?action=employeeList");
                    return;
                }
            }

            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }

    @Override
    public void destroy() {
        emf.close();
    }
}
