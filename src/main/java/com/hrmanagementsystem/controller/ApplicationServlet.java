package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.ApplicationDAO;
import com.hrmanagementsystem.dao.JobOfferDAO;
import com.hrmanagementsystem.entity.Application;
import com.hrmanagementsystem.entity.JobOffer;

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
import java.util.List;

@MultipartConfig
public class ApplicationServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        Application application = new Application(title, description, filePath, appliedDate, phoneNumber, jobOffer);
        ApplicationDAO.save(application);
        resp.getWriter().print("File uploaded successfully and path saved to the database!");
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
        }
    }

    public void addApplicationForm (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("view/addApplication.jsp").forward(req, resp);
    }

    public void getAllApplications(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int jobOfferId = Integer.parseInt(req.getParameter("jobOfferId"));
        List<Application> applications = ApplicationDAO.getAllByJobOfferId(jobOfferId);

        if (applications != null && !applications.isEmpty()) {
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
        } else {
            resp.getWriter().print("No applications found for this job offer.");
        }
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
}
