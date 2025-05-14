package com.workxlife.authentication_service.controller;

import com.workxlife.authentication_service.entity.Role;
import com.workxlife.authentication_service.entity.User;
import com.workxlife.authentication_service.repository.RoleRepository;
import com.workxlife.authentication_service.repository.UserRepository;
//import com.workxlife.authentication_service.repository.EmployeeRepository;
//import com.workxlife.authentication_service.entity.Employee;
import com.workxlife.authentication_service.security.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.workxlife.authentication_service.dto.EmployeeDto;
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

    //@Autowired
   // private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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





    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            //  Generate token with roles included
            String token = jwtUtil.generateToken(userDetails);

            // Return the token in response
            return ResponseEntity.ok(Map.of("token", token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}
