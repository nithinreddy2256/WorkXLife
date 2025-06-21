package com.workxlife.job_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private Long applicantId;

    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    public Long getApplicantId() {
        return applicantId;
    }

    public Job getJob() {
        return job;
    }

    @PrePersist
    protected void onApply() {
        this.appliedAt = LocalDateTime.now();
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

}
