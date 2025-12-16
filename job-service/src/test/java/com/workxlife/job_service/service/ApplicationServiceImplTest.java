package com.workxlife.job_service.service;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.entity.ApplicationStatus;
import com.workxlife.job_service.entity.Job;
import com.workxlife.job_service.exception.ResourceNotFoundException;
import com.workxlife.job_service.repository.ApplicationRepository;
import com.workxlife.job_service.repository.JobRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Application application;

    @BeforeEach
    void setUp() {
        Job job = new Job();
        job.setId(4L);

        application = new Application();
        application.setJob(job);
        application.setApplicantId(8L);
    }

    @Test
    void getApplicationsByJobIdDelegatesToRepository() {
        List<Application> applications = List.of(new Application());
        when(applicationRepository.findByJobId(1L)).thenReturn(applications);

        List<Application> result = applicationService.getApplicationsByJobId(1L);

        assertThat(result).isEqualTo(applications);
        verify(applicationRepository).findByJobId(1L);
    }

    @Test
    void applyForJobSavesApplicationWhenJobExists() {
        when(jobRepository.findById(4L)).thenReturn(Optional.of(new Job()));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(request.getHeader("Authorization")).thenReturn(null);

        Application saved = applicationService.applyForJob(application);

        assertThat(saved.getApplicantId()).isEqualTo(8L);
        verify(applicationRepository).save(application);
    }

    @Test
    void applyForJobThrowsWhenJobMissing() {
        when(jobRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.applyForJob(application))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job not found");
    }

    @Test
    void applyForJobValidatesPresenceOfJob() {
        Application withoutJob = new Application();

        assertThatThrownBy(() -> applicationService.applyForJob(withoutJob))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing job ID");
    }

    @Test
    void updateStatusPersistsUpdatedApplication() {
        Application existing = new Application();
        existing.setStatus(ApplicationStatus.PENDING);
        when(applicationRepository.findById(2L)).thenReturn(Optional.of(existing));

        applicationService.updateStatus(2L, ApplicationStatus.REVIEWED);

        assertThat(existing.getStatus()).isEqualTo(ApplicationStatus.REVIEWED);
        verify(applicationRepository).save(existing);
    }

    @Test
    void updateStatusThrowsWhenApplicationMissing() {
        when(applicationRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.updateStatus(3L, ApplicationStatus.REJECTED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Application not found");
    }

    @Test
    void deleteApplicationRemovesEntity() {
        Application existing = new Application();
        when(applicationRepository.findById(9L)).thenReturn(Optional.of(existing));

        applicationService.deleteApplication(9L);

        verify(applicationRepository).delete(existing);
    }
}
