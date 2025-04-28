package com.workxlife.authentication_service.config;

import com.workxlife.authentication_service.entity.Role;
import com.workxlife.authentication_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadRoles(RoleRepository roleRepository) {
        return args -> {
            createRoleIfNotExists(roleRepository, "ROLE_EMPLOYEE");
            createRoleIfNotExists(roleRepository, "ROLE_EMPLOYER");
            createRoleIfNotExists(roleRepository, "ROLE_ADMIN");
        };
    }

    private void createRoleIfNotExists(RoleRepository roleRepository, String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}

