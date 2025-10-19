package com.workxlife.job_service.client;

import com.workxlife.job_service.dto.EmployeeProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeProfileClient {

    @Autowired
    private RestTemplate restTemplate;

    public EmployeeProfileDTO fetchEmployee(Long employeeId, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<EmployeeProfileDTO> response = restTemplate.exchange(
                    "http://employee-service/api/employees/" + employeeId,
                    HttpMethod.GET,
                    entity,
                    EmployeeProfileDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("403 Forbidden from employee-service while fetching profile");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
