package com.workxlife.employee_service.service;

import com.workxlife.employee_service.model.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByEmail(String email);
    Employee updateEmployee(Long id, Employee updatedEmployee);
    void deleteEmployee(Long id);
    List<Employee> searchEmployeesByFirstName(String firstName);
    List<Employee> searchEmployeesByDepartment(String department);
    Page<Employee> getAllEmployeesPaginated(Pageable pageable);

}
