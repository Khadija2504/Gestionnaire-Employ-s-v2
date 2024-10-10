package com.hrmanagementsystem.controller;

import com.hrmanagementsystem.dao.EmployeeDAO;
import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.enums.Role;
import com.hrmanagementsystem.repository.implementations.EmployeeRepositoryImpl;
import com.hrmanagementsystem.service.EmployeeService;
import jakarta.servlet.annotation.WebServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        }
    }

    private void addEmployeeForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("views/addEmployee.jsp").forward(request, response);
    }

    private void editEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User employee = EmployeeDAO.getById(id);
        request.setAttribute("employee", employee);
        request.getRequestDispatcher("views/editEmployee.jsp").forward(request, response);
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
        int totalSalary = Integer.parseInt(request.getParameter("totalSalary"));
        String situation = request.getParameter("situation");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        Date hireDate = null;
        try {
            birthday = dateFormat.parse(birthdayStr);
            hireDate = dateFormat.parse(hireDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = new User(firstName, lastName, phoneNumber, salary, birthday, hireDate, position, kidsNum,
                totalSalary, situation, department, email, password, Role.Employee);

        EmployeeDAO.save(user);

        response.sendRedirect("employeeList?action=employeeList");
    }

    protected void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        EmployeeDAO.delete(id);
        response.sendRedirect("employeeList?action=employeeList");
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
        int totalSalary = Integer.parseInt(request.getParameter("totalSalary"));
        String situation = request.getParameter("situation");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        Date hireDate = null;
        try {
            birthday = dateFormat.parse(birthdayStr);
            hireDate = dateFormat.parse(hireDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User user = new User(firstName, lastName, phoneNumber, salary, birthday, hireDate, position, kidsNum,
                totalSalary, situation, department, email, password, Role.Employee);
        user.setId(id);
        EmployeeDAO.update(user);

        response.sendRedirect("employeeList?action=employeeList");
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
        request.getRequestDispatcher("views/employeeList.jsp").forward(request, response);
    }
}
