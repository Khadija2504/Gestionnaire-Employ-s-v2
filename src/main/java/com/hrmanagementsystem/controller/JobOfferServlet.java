package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.JobOfferDAO;
import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.enums.JobOfferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobOfferServlet extends HttpServlet {
    private ScheduledExecutorService scheduler;

    @Override
    public void init() throws ServletException {
        super.init();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
        super.destroy();
    }
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
            case "JobOfferList":
                JobOfferList(req, resp);
                break;
        }
    }

    private void addJobOfferForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("view/addJobOffer.jsp").forward(req, resp);
    }

    private void addJobOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate expiredDate = LocalDate.parse(req.getParameter("expiredDate"), formatter);

        LocalDateTime expiredDateTime = expiredDate.atTime(23, 59, 59);

        LocalDateTime publishedDate = LocalDateTime.now();
        JobOffer jobOffer = new JobOffer(title, description, publishedDate, expiredDate.atStartOfDay(), JobOfferStatus.Open);
        JobOfferDAO.save(jobOffer);
        long delay = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiredDateTime);
        scheduler.schedule(() -> updateJobOfferStatus(jobOffer.getId()), delay, TimeUnit.SECONDS);
        resp.sendRedirect("jobOffer?action=addJobOfferForm");
    }

    protected void deleteJobOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        JobOfferDAO.delete(id);
        resp.sendRedirect("JobOfferList?action=jobOfferList");
    }

    private void JobOfferList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<JobOffer> jobOffers = JobOfferDAO.getAll();
        req.setAttribute("jobOffers", jobOffers);
        req.getRequestDispatcher("view/DisplayAllJobOffers.jsp").forward(req, resp);
    }
    private void updateJobOfferStatus(int jobOfferId) {
        JobOffer jobOffer = JobOfferDAO.getById(jobOfferId);
        if (jobOffer != null && jobOffer.getStatus() == JobOfferStatus.Open) {
            jobOffer.setStatus(JobOfferStatus.Expired);
            JobOfferDAO.update(jobOffer);
        }
    }
}
