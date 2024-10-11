package com.hrmanagementsystem.controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.Role;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();

        if (isPublicResource(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!hasAccess(user.getRole(), requestURI)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicResource(String requestURI) {
        return requestURI.endsWith("login") ||
                requestURI.endsWith("loginForm") ||
                requestURI.endsWith("logout") ||
                requestURI.endsWith("LoginServlet") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg");
    }

    private boolean hasAccess(Role role, String requestURI) {
        switch (role) {
            case Admin:
                return requestURI.contains("employee");
            case RH:
                return requestURI.contains("/jobOffer") || requestURI.contains("/application/");
            case Employee:
                return requestURI.contains("/employee") || requestURI.contains("/jobOffer");
            case Recruiter:
                return requestURI.contains("/jobOffer") || requestURI.contains("/application");
            default:
                return false;
        }
    }
}
