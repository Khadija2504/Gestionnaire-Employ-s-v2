package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.implementations.EmployeeDAO;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.Role;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.annotation.WebServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/userServlet")
public class UserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "addEmployee":
                addEmployee(request, response);
                break;
            case "updateEmployee":
                updateEmployee(request, response);
                break;
//            case "searchEmployee":
//                searchEmployee(request, response);
//                break;
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "addEmployeeForm":
                addEmployeeForm(request, response);
                break;
            case "editEmployee":
                editEmployee(request, response);
                break;
            case "deleteEmployee":
                deleteEmployee(request, response);
                break;
            case "employeeList":
                displayEmployeesList(request, response);
                break;
            case "employeeProfile":
                profile(request, response);
                break;
            case "generateFamilyAllowanceStats":
                generateFamilyAllowanceStats(request, response);
                break;
        }
    }

    private void addEmployeeForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("view/AddEmployee.jsp").forward(request, response);
    }

    private void editEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User employee = EmployeeDAO.getById(id);
        request.setAttribute("employee", employee);
        request.getRequestDispatcher("view/editEmployee.jsp").forward(request, response);
    }

    private void generateFamilyAllowanceStats(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> employees = EmployeeDAO.getAll();
        Map<String, Map<String, Double>> monthlyStats = new HashMap<>();
        Map<Integer, Double> employeeStats = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        for (User employee : employees) {
            double totalAllowance = 0;
            for (int month = 1; month <= 12; month++) {
                double monthlyAllowance = calculateFamilyAllowanceReport(employee, employee.getSalary(), employee.getKidsNum());
                totalAllowance += monthlyAllowance;

                String monthKey = String.format("%04d-%02d", currentYear, month);
                monthlyStats.computeIfAbsent(monthKey, k -> new HashMap<>())
                        .merge("total", monthlyAllowance, Double::sum);
                monthlyStats.get(monthKey).merge("count", 1.0, Double::sum);
            }
            employeeStats.put(employee.getId(), totalAllowance);
        }

        request.setAttribute("monthlyStats", monthlyStats);
        request.setAttribute("employeeStats", employeeStats);
        request.setAttribute("employees", employees);
        request.getRequestDispatcher("view/Statistics.jsp").forward(request, response);
    }

    private void addEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        int salary = Integer.parseInt(request.getParameter("salary"));
        String birthdayStr = request.getParameter("birthday");
        String hireDateStr = request.getParameter("hireDate");
        String position = request.getParameter("position");
        int kidsNum = Integer.parseInt(request.getParameter("kidsNum"));
        String situation = request.getParameter("situation");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nssu = request.getParameter("nssu");

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        Date hireDate = null;
        try {
            birthday = dateFormat.parse(birthdayStr);
            hireDate = dateFormat.parse(hireDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int familyAllowance = calculateFamilyAllowance(salary, kidsNum);

        int totalSalary = salary + familyAllowance;

        User user = new User(firstName, lastName, phoneNumber, salary, birthday, hireDate, position, kidsNum,
                totalSalary, situation, department, email, hashedPassword, nssu, Role.Employee);

        boolean added = EmployeeDAO.save(user);
        if (added) {
            response.sendRedirect("employee?action=employeeList");
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Failed to add employee. Email or nsssu may already exist.");
            response.sendRedirect("employee?action=addEmployeeForm");
        }
    }

    protected void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        EmployeeDAO.delete(id);
        response.sendRedirect("employee?action=employeeList");
    }

    private int calculateFamilyAllowance(int salary, int kidsNum) {
        int allowancePerChild;
        int maxChildren = Math.min(kidsNum, 6);

        if (salary < 6000) {
            allowancePerChild = 300;
        } else if (salary > 8000) {
            allowancePerChild = 200;
        } else {
            double factor = (salary - 6000.0) / 2000.0;
            allowancePerChild = (int) (300 - (factor * 100));
        }

        int totalAllowance = 0;
        for (int i = 0; i < maxChildren; i++) {
            if (i < 3) {
                totalAllowance += allowancePerChild;
            } else {
                totalAllowance += (salary < 6000) ? 150 : 110;
            }
        }

        return totalAllowance;
    }

    private double calculateFamilyAllowanceReport(User employee, int salary, int kidsNum) {
        int allowancePerChild;
        int maxChildren = Math.min(kidsNum, 6);
        double baseAllowance = 100.0;
        double allowance = baseAllowance * employee.getKidsNum();

        if ("married".equalsIgnoreCase(employee.getSituation())) {
            allowance += 50.0;
        }
        if (salary < 6000) {
            allowancePerChild = 300;
        } else if (salary > 8000) {
            allowancePerChild = 200;
        } else {
            double factor = (salary - 6000.0) / 2000.0;
            allowancePerChild = (int) (300 - (factor * 100));
        }

        int totalAllowance = 0;
        for (int i = 0; i < maxChildren; i++) {
            if (i < 3) {
                totalAllowance += allowancePerChild;
            } else {
                totalAllowance += (salary < 6000) ? 150 : 110;
            }
        }

        return allowance + totalAllowance;
    }
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        int salary = Integer.parseInt(request.getParameter("salary"));
        String birthdayStr = request.getParameter("birthday");
        String hireDateStr = request.getParameter("hireDate");
        String position = request.getParameter("position");
        int kidsNum = Integer.parseInt(request.getParameter("kidsNum"));
        String situation = request.getParameter("situation");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nssu = request.getParameter("nssu");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        Date hireDate = null;
        try {
            birthday = dateFormat.parse(birthdayStr);
            hireDate = dateFormat.parse(hireDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int familyAllowance = calculateFamilyAllowance(salary, kidsNum);

        int totalSalary = salary + familyAllowance;

        User user = new User(firstName, lastName, phoneNumber, salary, birthday, hireDate, position, kidsNum,
                totalSalary, situation, department, email, password, nssu, Role.Employee);
        user.setId(id);
        boolean updated = EmployeeDAO.update(user);
        if (updated) {
            response.sendRedirect("employee?action=employeeList");
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Failed to update employee. Email may already exist.");
            response.sendRedirect("employee?action=editEmployee&id="+id);
        }
    }

//    private void searchEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<User> employeeList = getAll();
//        String searchQuery = request.getParameter("searchQuery");
//        List<User> result =  employeeList.stream()
//                .filter(employee -> (employee.getName().equalsIgnoreCase(searchQuery) ||
//                        employee.getPoste().equalsIgnoreCase(searchQuery) ||
//                        employee.getEmail().equalsIgnoreCase(searchQuery) ||
//                        employee.getDepartment().equalsIgnoreCase(searchQuery)))
//                .toList();
//        request.setAttribute("employees", result);
//        request.getRequestDispatcher("views/employeeList.jsp").forward(request, response);
//    }
    private void displayEmployeesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> employeeList = EmployeeDAO.getAll();
        request.setAttribute("employees", employeeList);
        System.out.println(employeeList);
        request.getRequestDispatcher("view/DisplayAllEmployees.jsp").forward(request, response);
    }

    public void profile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer loggedInUserId = (Integer) request.getSession().getAttribute("loggedInUserId");
        User employee = EmployeeDAO.getById(loggedInUserId);

        int familyAllowance = calculateFamilyAllowance(employee.getSalary(), employee.getKidsNum());

        Map<String, Object> familyAllowanceDetails = new HashMap<>();
        familyAllowanceDetails.put("amount", familyAllowance);
        familyAllowanceDetails.put("num_children", employee.getKidsNum());
        familyAllowanceDetails.put("situation", employee.getSituation());

        request.setAttribute("employee", employee);
        request.setAttribute("familyAllowanceDetails", familyAllowanceDetails);
        request.getRequestDispatcher("view/profile.jsp").forward(request, response);
    }
}
