package com.example.expense_tracker.config;

import com.example.expense_tracker.entity.Role;
import com.example.expense_tracker.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with default roles on application startup
 * if they do not already exist.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        List<String> defaultRoles = List.of("EMPLOYEE", "MANAGER", "ADMIN");

        for (String roleName : defaultRoles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("[DataInitializer] Created role: " + roleName);
            }
        }
    }
}
