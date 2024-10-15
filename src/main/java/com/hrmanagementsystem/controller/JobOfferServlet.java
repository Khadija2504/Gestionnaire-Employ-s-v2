package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.enums.JobOfferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        super.destroy();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
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

    protected void addJobOffer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String expiredDateStr = request.getParameter("expiredDate");
        System.out.println(title + " " + description);
        JobOffer jobOffer = new JobOffer(title,description, LocalDateTime.now(), LocalDate.parse(expiredDateStr).atStartOfDay(), JobOfferStatus.Open);
        System.out.println("job offer details:" + jobOffer);
        JobOfferDAO.save(jobOffer);

        long delayInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), jobOffer.getExpiredDate());
        scheduler.schedule(() -> {
            jobOffer.setStatus(JobOfferStatus.Expired);
            JobOfferDAO.update(jobOffer);
        }, delayInSeconds, TimeUnit.SECONDS);

        response.sendRedirect("jobOffer?action=addJobOfferForm");
    }

    public void deleteJobOffer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
