package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.service.EmployeeService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AdminInitializerListenerServlet implements ServletContextListener {
    protected EmployeeService employeeService;
    public AdminInitializerListenerServlet(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    private EntityManagerFactory emf;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        emf = (EntityManagerFactory) sce.getServletContext().getAttribute("emf");
        EntityManager em = emf.createEntityManager();
        try {
            employeeService.initializeAdminUser();
        } finally {
            em.close();
        }
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}