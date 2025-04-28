package com.workxlife.job_service.dto;

import com.workxlife.job_service.entity.EmploymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private BigDecimal salary;
    private EmploymentType employmentType;
    private Long postedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
