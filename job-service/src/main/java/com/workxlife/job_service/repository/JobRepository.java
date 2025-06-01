package com.workxlife.job_service.repository;

import com.workxlife.job_service.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByTitleContainingIgnoreCase(String title);
    List<Job> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Job> findByLocationContainingIgnoreCase(String location);
    List<Job> findByPostedBy(Long employerId);
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndCompanyNameContainingIgnoreCase(
            String title, String location, String companyName
    );
}
