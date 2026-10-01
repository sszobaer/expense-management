package com.example.expense_tracker.dto.response;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String department;
    private Boolean active;

    public UserResponse() {}

    public UserResponse(Long id, String name, String email, String role, String department, Boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
