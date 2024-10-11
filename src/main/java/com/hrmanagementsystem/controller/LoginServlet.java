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
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("hr_management_pu");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request, response);
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
                    System.out.println("User logged in successfully");
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    System.out.println("User role: " + user.getRole());

                    switch (user.getRole()) {
                        case Admin:
                            response.sendRedirect(request.getContextPath() + "/employee?action=employeeList");
                            break;
                        case RH:
                            response.sendRedirect(request.getContextPath() + "/jobOffer?action=jobOfferList");
                            break;
                        case Recruiter:
                            response.sendRedirect(request.getContextPath() + "/jobOffer?action=jobOfferList");
                        case Employee:
                            response.sendRedirect(request.getContextPath() + "/employee?action=employeeList");
                            break;
                    }
                } else {
                    request.setAttribute("error", "Invalid email or password");
                    request.getRequestDispatcher("login").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("login").forward(request, response);
            }
        } finally {
            em.close();
        }
    }
    @Override
    public void destroy() {
        emf.close();
    }
}
