package com.hrmanagementsystem.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class EmployeeServlet extends HttpServlet {
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//        switch (action) {
//            case "addEmployee":
//                addEmployee(request, response);
//                break;
//            case "updateEmployee":
//                updateEmployee(request, response);
//                break;
//            case "searchEmployee":
//                searchEmployee(request, response);
//                break;
//            case "filterEmployee":
//                filterEmployee(request, response);
//                break;
//        }
//    }
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//        switch (action) {
//            case "addEmployeeForm":
//                addEmployeeForm(request, response);
//                break;
//            case "editEmployee":
//                editEmployee(request, response);
//                break;
//            case "deleteEmployee":
//                deleteEmployee(request, response);
//                break;
//            case "employeeList":
//                displayEmployeesList(request, response);
//                break;
//        }
//    }
//
//    private void addEmployeeForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.getRequestDispatcher("views/addEmployee.jsp").forward(request, response);
//    }
//
//    private void editEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int id = Integer.parseInt(request.getParameter("id"));
//        Employee employee = EmployeeDAO.getById(id);
//        request.setAttribute("employee", employee);
//        request.getRequestDispatcher("views/editEmployee.jsp").forward(request, response);
//    }
//
//    private void addEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
//        String name = request.getParameter("name");
//        String email = request.getParameter("email");
//        String phone = request.getParameter("phone");
//        String department = request.getParameter("department");
//        String poste = request.getParameter("poste");
//
//        Employee employee = new Employee(name, email, phone, department, poste);
//        EmployeeDAO.save(employee);
//
//        response.sendRedirect("employeeList?action=employeeList");
//    }
//
//    protected void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int id = Integer.parseInt(request.getParameter("id"));
//        EmployeeDAO.delete(id);
//        response.sendRedirect("employeeList?action=employeeList");
//    }
//
//    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int id = Integer.parseInt(request.getParameter("id"));
//        String name = request.getParameter("name");
//        String email = request.getParameter("email");
//        String phone = request.getParameter("phone");
//        String department = request.getParameter("department");
//        String poste = request.getParameter("poste");
//
//        Employee employee = new Employee(name, email, phone, department, poste);
//        employee.setId(id);
//        EmployeeDAO.update(employee);
//
//        response.sendRedirect("employeeList?action=employeeList");
//    }
//    private void filterEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String poste = request.getParameter("poste");
//        List<Employee> employeeList = EmployeeDAO.getAll();
//        List<Employee> filteredEmployees = employeeList.stream()
//                .filter(employee -> poste == null || employee.getPoste().equalsIgnoreCase(poste))
//                .toList();
//        request.setAttribute("employees", filteredEmployees);
//        request.getRequestDispatcher("views/employeeList.jsp").forward(request, response);
//    }
//
//    private void startupRedirect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.sendRedirect(request.getContextPath() + "/employeeList?action=employeeList");
//    }
//
//    private void searchEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Employee> employeeList = getAll();
//        String searchQuery = request.getParameter("searchQuery");
//        List<Employee> result =  employeeList.stream()
//                .filter(employee -> (employee.getName().equalsIgnoreCase(searchQuery) ||
//                        employee.getPoste().equalsIgnoreCase(searchQuery) ||
//                        employee.getEmail().equalsIgnoreCase(searchQuery) ||
//                        employee.getDepartment().equalsIgnoreCase(searchQuery)))
//                .toList();
//        request.setAttribute("employees", result);
//        request.getRequestDispatcher("views/employeeList.jsp").forward(request, response);
//    }
//    private void displayEmployeesList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Employee> employeeList = EmployeeDAO.getAll();
//        request.setAttribute("employees", employeeList);
//        System.out.println(employeeList);
//        request.getRequestDispatcher("views/employeeList.jsp").forward(request, response);
//    }
}