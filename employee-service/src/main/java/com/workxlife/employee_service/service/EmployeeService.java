package com.workxlife.employee_service.service;

import com.workxlife.employee_service.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByEmail(String email);
    Employee updateEmployee(Long id, Employee updatedEmployee);
    void deleteEmployee(Long id);
}
