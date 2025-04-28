package com.workxlife.job_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service", url = "http://localhost:8082/api/employees")
public interface EmployeeClient {

    @GetMapping("/{id}")
    Object getEmployeeById(@PathVariable Long id);
}
