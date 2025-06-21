package com.workxlife.employee_service.service;

import com.workxlife.employee_service.exception.EmployeeNotFoundException;
import com.workxlife.employee_service.model.Employee;
import com.workxlife.employee_service.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;


import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImp(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> searchEmployeesByFirstName(String firstName) {
        return employeeRepository.findByFirstName(firstName);
    }

    @Override
    public List<Employee> searchEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCase(department);
    }

    @Override
    public Page<Employee> getAllEmployeesPaginated(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        // Check if the new email is different and already exists for another employee
        if (updatedEmployee.getEmail() != null && !updatedEmployee.getEmail().equals(existingEmployee.getEmail())) {
            Optional<Employee> employeeWithEmail = employeeRepository.findByEmail(updatedEmployee.getEmail());
            if (employeeWithEmail.isPresent() && !employeeWithEmail.get().getId().equals(existingEmployee.getId())) {
                throw new RuntimeException("Email is already taken by another employee.");
            }
        }

        if (updatedEmployee.getFirstName() != null) {
            existingEmployee.setFirstName(updatedEmployee.getFirstName());
        }
        if (updatedEmployee.getMiddleName() != null) {
            existingEmployee.setMiddleName(updatedEmployee.getMiddleName());
        }
        if (updatedEmployee.getLastName() != null) {
            existingEmployee.setLastName(updatedEmployee.getLastName());
        }
        if (updatedEmployee.getEmail() != null) {
            existingEmployee.setEmail(updatedEmployee.getEmail());
        }
        if (updatedEmployee.getDepartment() != null) {
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
        }
        if (updatedEmployee.getSkills() != null) {
            existingEmployee.setSkills(updatedEmployee.getSkills());
        }
        if (updatedEmployee.getBio() != null) {
            existingEmployee.setBio(updatedEmployee.getBio());
        }
        if (updatedEmployee.getExperienceYears() > 0) {
            existingEmployee.setExperienceYears(updatedEmployee.getExperienceYears());
        }

        if (updatedEmployee.getCurrentPosition() != null) {
            existingEmployee.setCurrentPosition(updatedEmployee.getCurrentPosition());
        }
        if (updatedEmployee.getLocation() != null) {
            existingEmployee.setLocation(updatedEmployee.getLocation());
        }
        if (updatedEmployee.getEducation() != null) {
            existingEmployee.setEducation(updatedEmployee.getEducation());
        }
        if (updatedEmployee.getCertifications() != null) {
            existingEmployee.setCertifications(updatedEmployee.getCertifications());
        }

        return employeeRepository.save(existingEmployee);
    }


    @Override
    public Employee uploadFiles(Long id, MultipartFile profileImage, MultipartFile resume) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        try {
            String uploadRoot = System.getProperty("user.dir") + "/uploads"; // Absolute path

            if (profileImage != null && !profileImage.isEmpty()) {
                String safeName = profileImage.getOriginalFilename().replaceAll("\\s+", "_");
                String filename = "profile_" + id + "_" + safeName;
                Path path = Paths.get(uploadRoot, "profile", filename);
                Files.createDirectories(path.getParent());

                System.out.println(" Saving profile image to: " + path.toAbsolutePath());
                Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                emp.setProfileImageUrl("/uploads/profile/" + filename);
            }

            if (resume != null && !resume.isEmpty()) {
                String safeName = resume.getOriginalFilename().replaceAll("\\s+", "_");
                String filename = "resume_" + id + "_" + safeName;
                String uploadDir = System.getProperty("user.dir") + "/uploads/resume/";
                Path path = Paths.get(uploadDir, filename);
                Files.createDirectories(path.getParent());

                System.out.println(" Incoming resume: " + resume.getOriginalFilename());
                System.out.println(" Saving resume to: " + path.toAbsolutePath());

                Files.copy(resume.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                emp.setResumeUrl("/uploads/resume/" + filename);
            }


            return employeeRepository.save(emp);

        } catch (IOException e) {
            System.err.println(" File upload failed: " + e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }
    }








    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
