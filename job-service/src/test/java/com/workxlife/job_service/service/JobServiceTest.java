package com.workxlife.job_service.service;

import com.workxlife.job_service.dto.JobDTO;
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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JobService jobService;

    private Job job;

    @BeforeEach
    void setUp() {
        job = new Job();
        job.setId(1L);
        job.setTitle("Developer");
        job.setLocation("Remote");
        job.setCompanyName("WorkXLife");
    }

    @Test
    void getJobByIdThrowsWhenMissing() {
        when(jobRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.getJobById(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job not found");
    }

    @Test
    void getJobByIdReturnsJobWhenPresent() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Job found = jobService.getJobById(1L);

        assertThat(found).isEqualTo(job);
        verify(jobRepository).findById(1L);
    }

    @Test
    void searchJobsNormalizesNullInputs() {
        jobService.searchJobs(null, null, null);

        verify(jobRepository).findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCompanyNameContainingIgnoreCase("", "", "");
    }

    @Test
    void createJobSavesEntityAndSendsNotification() throws Exception {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setTitle("Backend");
        jobDTO.setDescription("Build APIs");
        jobDTO.setCompanyName("WorkXLife");
        jobDTO.setLocation("Remote");
        jobDTO.setSalary(java.math.BigDecimal.valueOf(100000));
        jobDTO.setPostedBy(9L);

        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        String token = buildToken("user@example.com");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        Job created = jobService.createJob(jobDTO);

        assertThat(created.getId()).isEqualTo(99L);
        assertThat(created.getTitle()).isEqualTo("Backend");
        verify(jobRepository).save(any(Job.class));
        verify(rabbitTemplate).convertAndSend(eq("notification.queue"), any(String.class));
    }

    @Test
    void getJobsByEmployerDelegatesToRepository() {
        List<Job> jobs = List.of(job);
        when(jobRepository.findByPostedBy(5L)).thenReturn(jobs);

        List<Job> result = jobService.getJobsByEmployer(5L);

        assertThat(result).isEqualTo(jobs);
        verify(jobRepository).findByPostedBy(5L);
    }

    @Test
    void updateJobCopiesFields() {
        Job updates = new Job();
        updates.setTitle("Updated");
        updates.setDescription("New desc");
        updates.setCompanyName("NewCo");
        updates.setLocation("NY");
        updates.setSalary(java.math.BigDecimal.valueOf(120000));
        updates.setEmploymentType(job.getEmploymentType());

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job result = jobService.updateJob(1L, updates);

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.getCompanyName()).isEqualTo("NewCo");
        verify(jobRepository).save(job);
    }

    @Test
    void deleteJobRemovesEntity() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        jobService.deleteJob(1L);

        verify(jobRepository).delete(job);
    }

    private String buildToken(String subject) {
        Key key = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12345".getBytes());
        return Jwts.builder()
                .setSubject(subject)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
