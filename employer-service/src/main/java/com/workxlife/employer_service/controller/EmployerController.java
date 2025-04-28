package com.workxlife.employer_service.controller;

import com.workxlife.employer_service.model.Employer;
import com.workxlife.employer_service.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {
    @Autowired
    private EmployerService employerService;

    @PostMapping
    public ResponseEntity<Employer> createEmployer(@RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.createEmployer(employer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employer> getEmployerById(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getEmployerById(id));
    }

    @GetMapping
    public ResponseEntity<List<Employer>> getAllEmployers() {
        return ResponseEntity.ok(employerService.getAllEmployers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employer> updateEmployer(@PathVariable Long id, @RequestBody Employer employer) {
        return ResponseEntity.ok(employerService.updateEmployer(id, employer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployer(@PathVariable Long id) {
        employerService.deleteEmployer(id);
        return ResponseEntity.ok("Employer deleted successfully");
    }
}
