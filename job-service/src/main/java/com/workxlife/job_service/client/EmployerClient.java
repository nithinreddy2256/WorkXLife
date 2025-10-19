package com.workxlife.job_service.client;

import com.workxlife.job_service.dto.EmployerSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployerClient {

    @Autowired
    private RestTemplate restTemplate;

    private String fetchInternalToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("username", "internal-service");
        body.put("password", "super-secret");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://authentication-service/api/auth/internal-auth",
                request,
                Map.class
        );

        return (String) response.getBody().get("token");
    }

    public boolean employerExists(Long employerId) {
        try {
            String token = fetchInternalToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "http://employer-service/api/employers/" + employerId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("403 Forbidden from employer-service");
            return false;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public EmployerSummaryDTO fetchEmployerDetails(Long employerId) {
        try {
            String token = fetchInternalToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<EmployerSummaryDTO> response = restTemplate.exchange(
                    "http://employer-service/api/employers/profile/" + employerId,
                    HttpMethod.GET,
                    entity,
                    EmployerSummaryDTO.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println("403 Forbidden from employer-service while fetching employer details");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
