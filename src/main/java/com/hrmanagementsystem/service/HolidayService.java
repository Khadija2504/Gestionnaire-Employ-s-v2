package com.hrmanagementsystem.service;

import com.hrmanagementsystem.dao.HolidayDAO;
import com.hrmanagementsystem.entity.Holiday;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.HolidayStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HolidayService {
    static void addHoliday(Date startDate, Date endDate, String reason, String filePath, User employee) {
        Holiday holiday = new Holiday(startDate, endDate, reason, filePath, HolidayStatus.Pending, employee);
        HolidayDAO.save(holiday);
    }

    static Map<String, Map<String, Object>> generateMonthlyAbsenceReport(int year, int month) {
        return HolidayDAO.getMonthlyAbsenceReport(year, month);
    }

    static List<Holiday> getAcceptedHolidaysForEmployee(User employee) {
        return HolidayDAO.getAcceptedHolidaysForEmployee(employee);
    }

    static List<Holiday> getAllHolidays() {
        return HolidayDAO.getAllHolidays();
    }

    static Holiday getById(int id) {
        return HolidayDAO.getById(id);
    }

    static void update(int holidayId, String newStatus) {
        Holiday holiday = HolidayDAO.getById(holidayId);
        if (holiday != null) {
            holiday.setStatus(HolidayStatus.valueOf(newStatus));
            HolidayDAO.update(holiday);
        }
    }

    static String getJustification(int holidayId) {
        Holiday holiday = HolidayDAO.getById(holidayId);
        return holiday != null ? holiday.getJustification() : null;
    }
}
