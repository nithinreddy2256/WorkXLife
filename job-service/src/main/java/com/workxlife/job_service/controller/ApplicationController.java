package com.workxlife.job_service.controller;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.service.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.workxlife.job_service.dto.ApplicationDetailsDto;
import java.util.stream.Collectors;
import org.springframework.web.reactive.function.client.WebClient;
import com.workxlife.job_service.dto.EmployeeDto;
import com.workxlife.job_service.dto.ApplicationDetailsDto;
import org.springframework.beans.factory.annotation.Qualifier;
import com.workxlife.job_service.entity.ApplicationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Objects;




import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationServiceImpl applicationService;

    @Autowired
    @Qualifier("loadBalancedWebClientBuilder")
    private WebClient.Builder webClientBuilder;

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);


    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationDetailsDto>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authHeader
    ) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId);

        List<ApplicationDetailsDto> result = applications.stream()
                .map(application -> {
                    Long applicantId = application.getApplicantId();

                    try {
                        EmployeeDto employee = webClientBuilder.build()
                                .get()
                                .uri("http://employee-service/api/employees/" + applicantId)
                                .header("Authorization", authHeader)
                                .retrieve()
                                .bodyToMono(EmployeeDto.class)
                                .block();

                        if (employee == null) {
                            // Just in case .block() returns null
                            log.warn("Employee not found for applicantId {}", applicantId);
                            return null;
                        }

                        return new ApplicationDetailsDto(
                                applicantId,
                                employee.getFirstName() + " " + employee.getLastName(),
                                application.getAppliedAt(),
                                application.getJob().getTitle(),
                                application.getJob().getCompanyName(),
                                application.getStatus(),
                                application.getId()
                        );

                    } catch (WebClientResponseException.NotFound e) {
                        log.warn("Employee with ID {} not found. Skipping application.", applicantId);
                        return null; // Skip this application
                    } catch (Exception e) {
                        log.error("Error retrieving employee with ID {}: {}", applicantId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }




    @GetMapping("/applicant/{applicantId}")
    public List<Application> getApplicationsByApplicantId(@PathVariable Long applicantId) {
        return applicationService.getApplicationsByApplicantId(applicantId);
    }

    @PostMapping
    public ResponseEntity<Application> applyForJob(@Valid @RequestBody Application application) {
        return ResponseEntity.ok(applicationService.applyForJob(application));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok("Application deleted successfully.");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam("status") ApplicationStatus status
    ) {
        applicationService.updateStatus(id, status);
        return ResponseEntity.ok("Application status updated to " + status);
    }

}
