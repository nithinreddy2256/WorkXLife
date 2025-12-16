package com.workxlife.employer_service.service;

import com.workxlife.employer_service.exception.ResourceNotFoundException;
import com.workxlife.employer_service.model.Employer;
import com.workxlife.employer_service.repository.EmployerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceImplTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerServiceImpl employerService;

    @Test
    void createEmployerDelegatesToRepository() {
        Employer employer = new Employer();
        when(employerRepository.save(employer)).thenReturn(employer);

        Employer saved = employerService.createEmployer(employer);

        assertThat(saved).isSameAs(employer);
        verify(employerRepository).save(employer);
    }

    @Test
    void getAllEmployersReturnsRepositoryData() {
        List<Employer> employers = List.of(new Employer(), new Employer());
        when(employerRepository.findAll()).thenReturn(employers);

        List<Employer> result = employerService.getAllEmployers();

        assertThat(result).containsExactlyElementsOf(employers);
        verify(employerRepository).findAll();
    }

    @Test
    void updateEmployerCopiesFieldsFromRequest() {
        Employer existing = new Employer();
        existing.setId(1L);
        when(employerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(employerRepository.save(any(Employer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employer updates = new Employer();
        updates.setCompanyName("Acme Corp");
        updates.setIndustry("Tech");
        updates.setWebsite("https://acme.example");
        updates.setLocation("NYC");
        updates.setSize("100");
        updates.setEstablishedYear("2000");
        updates.setAbout("About");
        updates.setContactName("Jane");
        updates.setContactEmail("jane@acme.com");
        updates.setContactPhone("123");
        updates.setRecruiterName("Recruiter");
        updates.setRecruiterRole("Lead");
        updates.setRecruiterBio("Bio");

        Employer result = employerService.updateEmployer(1L, updates);

        assertThat(result.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(result.getRecruiterBio()).isEqualTo("Bio");
        verify(employerRepository).save(existing);
    }

    @Test
    void getEmployerByIdThrowsWhenMissing() {
        when(employerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerService.getEmployerById(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employer not found");
    }

    @Test
    void deleteEmployerRemovesEntity() {
        Employer existing = new Employer();
        when(employerRepository.findById(3L)).thenReturn(Optional.of(existing));

        employerService.deleteEmployer(3L);

        verify(employerRepository).delete(existing);
    }
}
