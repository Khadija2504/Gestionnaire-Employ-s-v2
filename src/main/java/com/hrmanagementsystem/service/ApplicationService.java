package com.hrmanagementsystem.service;

import com.hrmanagementsystem.dao.implementations.ApplicationDAO;
import com.hrmanagementsystem.dao.implementations.JobOfferDAO;
import com.hrmanagementsystem.entity.Application;
import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.enums.ApplicationStatus;
import com.hrmanagementsystem.interfaces.ApplicationInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class ApplicationService {

    public Application getById(int id) {

    }

    public void save(String title, String description, String phoneNumber, LocalDateTime appliedDate, String uploadFilePath, int jobOfferId, Part filePart) {
        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String fileName = extractFileName(filePart);

        String filePath = uploadFilePath + File.separator + fileName;
        filePart.write(filePath);

        JobOffer jobOffer = JobOfferDAO.getById(jobOfferId);

        if (jobOffer == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Job Offer ID");
            return;
        }
        Application application = new Application(title, description, filePath, appliedDate, phoneNumber, jobOffer, ApplicationStatus.Pending);
        ApplicationDAO.save(application);
    }

    public List<Application> getAllByJobOfferId(int jobOfferId) {

    }

    public List<Application> getFilteredApplications(int jobOfferId, ApplicationStatus status) {

    }

    public void updateStatus(Application application) {

    }
}
