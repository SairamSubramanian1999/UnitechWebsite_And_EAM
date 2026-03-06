package com.unitech.employee.dto;

import com.unitech.employee.model.Role;

public class RegistrationDto {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;

    // Employee fields to create Employee row at registration
    private String department;
    private String jobTitle;
    private String phone;
    private String dateOfJoining; // keep as String to accept yyyy-MM-dd from HTML date input
    private String status; // ACTIVE / RESIGNED / INACTIVE

    public RegistrationDto() {}

    // getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(String dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
