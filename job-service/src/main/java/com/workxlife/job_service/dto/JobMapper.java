package com.workxlife.job_service.dto;

import com.workxlife.job_service.entity.Application;
import com.workxlife.job_service.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JobMapper {
    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    @Mapping(target = "jobId", source = "job.id")
    ApplicationDTO applicationToDTO(Application application);

    JobDTO jobToDTO(Job job);
    Job dtoToJob(JobDTO jobDTO);
}