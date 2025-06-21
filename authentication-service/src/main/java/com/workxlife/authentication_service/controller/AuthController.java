package com.workxlife.authentication_service.controller;

import com.workxlife.authentication_service.dto.AuthRequest;
import com.workxlife.authentication_service.entity.Role;
import com.workxlife.authentication_service.repository.RoleRepository;
import com.workxlife.authentication_service.repository.UserRepository;
import com.workxlife.authentication_service.security.JwtUtil;
import com.workxlife.authentication_service.entity.User;
import com.workxlife.authentication_service.dto.EmployeeDto;
import com.workxlife.authentication_service.dto.AuthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String roleName = request.get("role");

        String firstName = request.getOrDefault("firstName", username);
        String middleName = request.getOrDefault("middleName", "");
        String lastName = request.getOrDefault("lastName", "");
        String department = request.getOrDefault("department", "General");

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        // Save to authentication DB
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(role.get()));
        userRepository.save(user);

        // Send to employee-service only if role is employee
        if ("ROLE_EMPLOYEE".equals(roleName)) {
            try {
                Map<String, String> employeePayload = new HashMap<>();
                employeePayload.put("firstName", firstName);
                employeePayload.put("middleName", middleName);
                employeePayload.put("lastName", lastName);
                employeePayload.put("email", email);
                employeePayload.put("department", department);

                restTemplate.postForObject("http://localhost:8082/api/employees", employeePayload, String.class);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("User saved but failed to create employee profile: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/internal-auth")
    public ResponseEntity<Map<String, String>> generateInternalToken(@RequestBody AuthRequest request) {
        if (!request.getUsername().equals("internal-service") || !request.getPassword().equals("super-secret")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "internal-service",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
        );

        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails); // Includes roles

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Long employeeId = null;

            if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_EMPLOYEE"))) {
                System.out.println("User has ROLE_EMPLOYEE. Trying to fetch employee ID...");

                String internalToken = jwtUtil.generateTokenWithInternalRole(user.getUsername());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + internalToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                String targetUrl = "http://localhost:8082/api/employees/email/" + user.getEmail();



                System.out.println("Calling: " + targetUrl);

                try {
                    ResponseEntity<EmployeeDto> empResponse = restTemplate.exchange(
                            targetUrl,
                            HttpMethod.GET,
                            entity,
                            EmployeeDto.class
                    );

                    System.out.println("Status from employee-service: " + empResponse.getStatusCode());
                    System.out.println("Response body from employee-service: " + empResponse.getBody());

                    if (empResponse.getStatusCode().is2xxSuccessful() && empResponse.getBody() != null) {
                        employeeId = empResponse.getBody().getId();
                        System.out.println("Extracted employeeId: " + employeeId);
                    } else {
                        System.err.println("Employee response was not successful or body was null.");
                    }
                } catch (Exception ex) {
                    System.err.println("Exception while calling employee-service: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            return ResponseEntity.ok(new AuthResponse(token, user.getId(), employeeId));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }



}
