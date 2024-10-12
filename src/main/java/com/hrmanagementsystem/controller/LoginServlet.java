package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.UserDAO;
import com.hrmanagementsystem.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
                    int userId = user.getId();
                    request.getSession().setAttribute("loggedInUserId", userId);
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
                        case Recruiter, Employee:
                            response.sendRedirect(request.getContextPath() + "/jobOffer?action=JobOfferList");
                            break;
                        default:
                            System.out.println("Unknown role: " + user.getRole());
                            request.setAttribute("errorMessage", "Invalid user role");
                            request.getRequestDispatcher("view/login.jsp").forward(request, response);
                    }
                } else {
                    System.out.println("Password does not match");
                    request.setAttribute("errorMessage", "Invalid email or password");
                    request.getRequestDispatcher("view/login.jsp").forward(request, response);
                }
            } else {
                System.out.println("User not found");
                request.setAttribute("errorMessage", "Invalid email or password");
                request.getRequestDispatcher("view/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Exception during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred during login");
            request.getRequestDispatcher("view/login.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
    @Override
    public void destroy() {
        emf.close();
    }
}
