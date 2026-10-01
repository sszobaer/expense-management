package com.example.expense_tracker.config;

import com.example.expense_tracker.entity.Department;
import com.example.expense_tracker.entity.ExpenseCategory;
import com.example.expense_tracker.entity.Role;
import com.example.expense_tracker.repository.DepartmentRepository;
import com.example.expense_tracker.repository.ExpenseCategoryRepository;
import com.example.expense_tracker.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with default roles, departments, and expense categories
 * on application startup. Records are only created if they do not already exist.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ExpenseCategoryRepository categoryRepository;

    public DataInitializer(
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            ExpenseCategoryRepository categoryRepository
    ) {
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedDepartments();
        seedCategories();
    }

    private void seedRoles() {
        List<String> defaultRoles = List.of("EMPLOYEE", "MANAGER", "ADMIN", "ACCOUNTANT");
        for (String roleName : defaultRoles) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("[DataInitializer] Created role: " + roleName);
            }
        }
    }

    private void seedDepartments() {
        List<String> defaultDepartments = List.of("Engineering", "Finance", "HR", "Marketing");
        for (String name : defaultDepartments) {
            if (departmentRepository.findByName(name).isEmpty()) {
                Department dept = new Department();
                dept.setName(name);
                departmentRepository.save(dept);
                System.out.println("[DataInitializer] Created department: " + name);
            }
        }
    }

    private void seedCategories() {
        List<String> defaultCategories = List.of(
                "Travel", "Food", "Office Supplies", "Equipment",
                "Software Subscription", "Accommodation", "Transportation"
        );
        for (String name : defaultCategories) {
            if (categoryRepository.findByName(name).isEmpty()) {
                ExpenseCategory category = new ExpenseCategory();
                category.setName(name);
                categoryRepository.save(category);
                System.out.println("[DataInitializer] Created category: " + name);
            }
        }
    }
}
