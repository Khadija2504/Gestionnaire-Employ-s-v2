package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.util.AdminInitializer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

    @WebListener
    public class AdminInitializerListenerServlet implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
            AdminInitializer.initializeAdminUser();
        }
    }