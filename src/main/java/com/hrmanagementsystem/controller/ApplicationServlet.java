package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.ApplicationDAO;
import com.hrmanagementsystem.dao.HolidayDAO;
import com.hrmanagementsystem.dao.JobOfferDAO;
import com.hrmanagementsystem.dao.NotificationDAO;
import com.hrmanagementsystem.entity.Application;
import com.hrmanagementsystem.entity.Holiday;
import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.entity.Notification;
import com.hrmanagementsystem.enums.ApplicationStatus;
import com.hrmanagementsystem.enums.HolidayStatus;
import com.hrmanagementsystem.util.EmailSender;

import javax.servlet.annotation.MultipartConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@MultipartConfig
public class ApplicationServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addApplication":
                addApplication(req, resp);
                break;
            case "filterApplications":
                filterApplications(req, resp);
                break;
            case "updateApplicationStatus":
                updateApplicationStatus(req, resp);
                break;
        }
    }

    private void addApplication(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("candidateName");
        String description = req.getParameter("candidateEmail");
        String phoneNumber = req.getParameter("phoneNumber");
        LocalDateTime appliedDate = LocalDateTime.now();
        String applicationPath = req.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Part filePart = req.getPart("resume");
        String fileName = extractFileName(filePart);

        String filePath = uploadFilePath + File.separator + fileName;
        filePart.write(filePath);

        int jobOfferId = Integer.parseInt(req.getParameter("jobOfferId"));

        JobOffer jobOffer = JobOfferDAO.getById(jobOfferId);

        if (jobOffer == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Job Offer ID");
            return;
        }
        Application application = new Application(title, description, filePath, appliedDate, phoneNumber, jobOffer, ApplicationStatus.Pending);
        ApplicationDAO.save(application);
        resp.sendRedirect("jobOffer?action=JobOfferList");
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addApplicationForm":
                addApplicationForm(req, resp);
                break;
            case "getAllApplications":
                getAllApplications(req, resp);
                break;
            case "downloadResume":
                downloadResume(req, resp);
                break;
            case "filterApplications":
                filterApplications(req, resp);
                break;
        }
    }

    public void addApplicationForm (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("view/addApplication.jsp").forward(req, resp);
    }

    public void getAllApplications(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int jobOfferId = Integer.parseInt(req.getParameter("jobOfferId"));
        List<Application> applications = ApplicationDAO.getAllByJobOfferId(jobOfferId);

            for (Application app : applications) {
                File resumeFile = new File(app.getResume());
                if (resumeFile.exists()) {
                    app.setResume(resumeFile.getName());
                } else {
                    app.setResume("Resume not found");
                }
            }

            req.setAttribute("applications", applications);
            req.getRequestDispatcher("view/DisplayAllApplications.jsp").forward(req, resp);
    }

    public void filterApplications(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int jobOfferId = Integer.parseInt(req.getParameter("jobOfferId"));
        String statusParam = req.getParameter("status");
        ApplicationStatus status = ApplicationStatus.valueOf(statusParam);

        List<Application> filteredApplications = ApplicationDAO.getFilteredApplications(jobOfferId, status);

            for (Application app : filteredApplications) {
                File resumeFile = new File(app.getResume());
                if (resumeFile.exists()) {
                    app.setResume(resumeFile.getName());
                } else {
                    app.setResume("Resume not found");
                }
            }

            req.setAttribute("applications", filteredApplications);
            req.getRequestDispatcher("view/DisplayAllApplications.jsp").forward(req, resp);
    }

    public void downloadResume(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int applicationId = Integer.parseInt(req.getParameter("applicationId"));
        Application application = ApplicationDAO.getbyId(applicationId);

        if (application != null && application.getResume() != null) {
            File downloadFile = new File(application.getResume());
            if (downloadFile.exists()) {
                FileInputStream inStream = new FileInputStream(downloadFile);

                resp.setContentType("application/octet-stream");
                resp.setHeader("Content-Disposition", "attachment;filename=" + downloadFile.getName());

                OutputStream outStream = resp.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                inStream.close();
                outStream.close();
            } else {
                resp.getWriter().print("Resume file not found!");
            }
        } else {
            resp.getWriter().print("Application or resume not found!");
        }
    }
    private void updateApplicationStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int appId = Integer.parseInt(req.getParameter("applicationId"));
        String newStatus = req.getParameter("status");
        int jobOfferId = Integer.parseInt(req.getParameter("jobOfferId"));

        Application application = ApplicationDAO.getbyId(appId);
        if (application == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found");
            return;
        }

        try {
            ApplicationStatus status = ApplicationStatus.valueOf(newStatus);
            application.setStatus(status);
            ApplicationDAO.updateStatus(application);

            String candidateEmail = application.getCandidateEmail();
            if(Objects.equals(status.toString(), "Accepted")) {
                String message = "ur application has been accepted for the job offer that u applied !!";
                EmailSender.sendEmail(candidateEmail, "Application accepted", message);

                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setNotificationDate(new Date());
                NotificationDAO.save(notification);

                resp.sendRedirect("application?action=getAllApplications&jobOfferId=" + jobOfferId);
            } else {
                String message = "ur application has been refused for the job offer that u applied, good luck in the next job offer!";
                EmailSender.sendEmail(candidateEmail, "Application refused", message);

                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setNotificationDate(new Date());
                NotificationDAO.save(notification);

                resp.sendRedirect("application?action=getAllApplications&jobOfferId=" + jobOfferId);
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status");
        }
    }
}
