package com.workxlife.application_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity  // Marks this class as a database entity (table)
@Data  // Lombok annotation to generate getters, setters, and toString() automatically
@NoArgsConstructor  // Generates a no-argument constructor
@AllArgsConstructor  // Generates an all-argument constructor
@Builder  // Helps in object creation using a builder pattern
public class Employee {

    @Id  // Marks this as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increments the ID
    private Long id;

    private String firstName;
    private String middleName;  // Can be null if the employee doesn't have a middle name
    private String lastName;

    private String email;
    private String department;
}
