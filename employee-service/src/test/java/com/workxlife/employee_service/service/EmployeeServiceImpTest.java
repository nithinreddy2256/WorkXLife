package com.workxlife.employee_service.service;

import com.workxlife.employee_service.exception.EmployeeNotFoundException;
import com.workxlife.employee_service.model.Employee;
import com.workxlife.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImpTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImp employeeService;

    private String originalUserDir;

    @AfterEach
    void restoreUserDir() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void saveEmployeeDelegatesToRepository() {
        Employee employee = new Employee();
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee saved = employeeService.saveEmployee(employee);

        assertThat(saved).isSameAs(employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void getAllEmployeesReturnsRepositoryResults() {
        List<Employee> employees = Arrays.asList(new Employee(), new Employee());
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).containsExactlyElementsOf(employees);
        verify(employeeRepository).findAll();
    }

    @Test
    void getAllEmployeesPaginatedUsesRepository() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(new Employee()));
        when(employeeRepository.findAll(pageRequest)).thenReturn(page);

        Page<Employee> result = employeeService.getAllEmployeesPaginated(pageRequest);

        assertThat(result.getContent()).hasSize(1);
        verify(employeeRepository).findAll(pageRequest);
    }

    @Test
    void updateEmployeeCopiesFieldsAndSaves() {
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee updates = new Employee();
        updates.setFirstName("John");
        updates.setEmail("new@example.com");
        updates.setExperienceYears(3);

        Employee result = employeeService.updateEmployee(1L, updates);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getExperienceYears()).isEqualTo(3);
        verify(employeeRepository).save(existing);
    }

    @Test
    void updateEmployeeThrowsWhenEmailTaken() {
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setEmail("old@example.com");

        Employee another = new Employee();
        another.setId(2L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employeeRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(another));

        Employee updates = new Employee();
        updates.setEmail("duplicate@example.com");

        assertThatThrownBy(() -> employeeService.updateEmployee(1L, updates))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email is already taken");
    }

    @Test
    void uploadFilesStoresProfileAndResume(@TempDir Path tempDir) throws IOException {
        originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());

        Employee employee = new Employee();
        employee.setId(5L);
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile profile = new MockMultipartFile("profile", "photo.jpg", "image/jpeg", "profile".getBytes());
        MockMultipartFile resume = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "resume".getBytes());

        Employee saved = employeeService.uploadFiles(5L, profile, resume);

        assertThat(saved.getProfileImageUrl()).contains("/uploads/profile/profile_5_photo.jpg");
        assertThat(saved.getResumeUrl()).contains("/uploads/resume/resume_5_resume.pdf");

        Path profilePath = tempDir.resolve("uploads/profile/profile_5_photo.jpg");
        Path resumePath = tempDir.resolve("uploads/resume/resume_5_resume.pdf");
        assertThat(Files.exists(profilePath)).isTrue();
        assertThat(Files.exists(resumePath)).isTrue();

        verify(employeeRepository).findById(5L);
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteEmployeeRemovesExistingRecord() {
        when(employeeRepository.existsById(10L)).thenReturn(true);

        employeeService.deleteEmployee(10L);

        verify(employeeRepository).deleteById(10L);
    }

    @Test
    void deleteEmployeeThrowsWhenNotFound() {
        when(employeeRepository.existsById(11L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(11L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("11");
    }
}
