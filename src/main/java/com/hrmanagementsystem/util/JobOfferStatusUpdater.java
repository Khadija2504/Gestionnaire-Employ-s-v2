package com.hrmanagementsystem.util;

import com.hrmanagementsystem.entity.JobOffer;
import com.hrmanagementsystem.enums.JobOfferStatus;
import com.hrmanagementsystem.service.JobOfferService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class JobOfferStatusUpdater implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    protected JobOfferService jobOfferService;
    public JobOfferStatusUpdater(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }
    @Override
    public void contextInitialized(ServletContextEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateExpiredJobOffers, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }

    private void updateExpiredJobOffers() {
        List<JobOffer> openJobOffers = jobOfferService.getByStatus(JobOfferStatus.Open);
        LocalDateTime now = LocalDateTime.now();

        for (JobOffer offer : openJobOffers) {
            if (offer.getExpiredDate().isBefore(now)) {
                offer.setStatus(JobOfferStatus.Expired);
                jobOfferService.update(offer);
            }
        }
    }
}