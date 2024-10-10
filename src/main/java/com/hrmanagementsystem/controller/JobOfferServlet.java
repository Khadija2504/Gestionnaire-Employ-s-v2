package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.JobOfferDAO;
import com.hrmanagementsystem.entity.JobOffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class JobOfferServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addJobOffer":
                addJobOffer(req,resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addJobOfferForm":
                addJobOfferForm(req, resp);
                break;
            case "deleteJobOffer":
                deleteJobOffer(req, resp);
                break;
        }
    }

    private void addJobOfferForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("view/addJobOffer.jsp").forward(req, resp);
    }

    private void addJobOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        LocalDateTime publishedDate = LocalDateTime.now();
        JobOffer jobOffer = new JobOffer(title, description, publishedDate);
        JobOfferDAO.save(jobOffer);
        resp.sendRedirect("addJobOffer?action=addJobOffer");
    }

    protected void deleteJobOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        JobOfferDAO.delete(id);
        resp.sendRedirect("JobOfferList?action=jobOfferList");
    }
}
