package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.EmployeeDAO;
import com.hrmanagementsystem.entity.Holiday;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.HolidayStatus;
import com.hrmanagementsystem.service.HolidayService;
import com.hrmanagementsystem.service.implementation.HolidayServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@MultipartConfig
public class HolidayServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addHoliday":
                addHoliday(req, resp);
                break;
            case "updateHoliday":
                updateHoliday(req, resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action) {
            case "addHolidayForm":
                addHolidayForm(req, resp);
                break;
            case "getAllHolidays":
                getAllHolidays(req, resp);
                break;
            case "downloadJustification":
                downloadJustification(req, resp);
                break;
        }
    }

    private void addHoliday(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");
        String reason = req.getParameter("reason");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dateFormat.parse(startDateStr);
            endDate = dateFormat.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String holidayPath = req.getServletContext().getRealPath("");
        String uploadFilePath = holidayPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Part filePart = req.getPart("justification");
        String fileName = extractFileName(filePart);

        String filePath = uploadFilePath + File.separator + fileName;
        filePart.write(filePath);

        Integer loggedInUserId = (Integer) req.getSession().getAttribute("loggedInUserId");
        User employee = EmployeeDAO.getById(loggedInUserId);

        if (employee == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID");
            return;
        }
        HolidayService.addHoliday(startDate, endDate, reason, filePath, employee);
        resp.sendRedirect("holidays?action=getAllHolidays");
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

    private void addHolidayForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("view/addHoliday.jsp").forward(req, resp);
    }

    private void getAllHolidays(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Holiday> holidays = HolidayService.getAllHolidays();
        req.setAttribute("holidays", holidays);
        req.getRequestDispatcher("view/displayAllHolidays.jsp").forward(req, resp);
    }
    private void editHoliday(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Holiday holiday = HolidayService.getById(id);
    }

    public void downloadJustification(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int holidayId = Integer.parseInt(req.getParameter("holidayId"));
        Holiday holiday = HolidayService.getById(holidayId);

        if (holiday != null && holiday.getJustification() != null) {
            File downloadFile = new File(holiday.getJustification());
            if (downloadFile.exists()) {
                FileInputStream inStream = new FileInputStream(downloadFile);

                resp.setContentType("holiday/octet-stream");
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
                resp.getWriter().print("justification file not found!");
            }
        } else {
            resp.getWriter().print("holiday or justification not found!");
        }
    }

    private void updateHoliday(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int holidayId = Integer.parseInt(req.getParameter("holidayId"));
        String newStatus = req.getParameter("status");

        try {
            HolidayService.update(holidayId, newStatus);
            resp.sendRedirect("holidays?action=getAllHolidays");
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status");
        }
    }
}
