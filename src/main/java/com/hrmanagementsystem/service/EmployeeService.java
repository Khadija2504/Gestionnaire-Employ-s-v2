package com.hrmanagementsystem.service;

import com.hrmanagementsystem.entity.User;
import com.hrmanagementsystem.repository.implementations.EmployeeRepositoryImpl;

public class EmployeeService {
    private EmployeeRepositoryImpl employeeRepository;

    public EmployeeService(EmployeeRepositoryImpl employeeRepository) {
    }

    public void UserService(EmployeeRepositoryImpl employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void registerUser(User user) {
        employeeRepository.addUser(user);
    }
}
