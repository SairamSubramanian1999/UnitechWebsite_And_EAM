package com.unitech.employee.service;

import com.unitech.employee.dto.RegistrationDto;
import com.unitech.employee.model.Employee;
import com.unitech.employee.model.Role;
import com.unitech.employee.model.User;
import com.unitech.employee.repository.EmployeeRepository;
import com.unitech.employee.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository repo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo,
                       EmployeeRepository employeeRepo,
                       PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.employeeRepo = employeeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------------------------------------------
    // ✔ REGISTER NEW USER + CREATE EMPLOYEE ROW
    // -------------------------------------------------------
    public User register(RegistrationDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Missing registration data");

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("Email required");

        String email = dto.getEmail().trim();

        if (repo.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered");

        if (dto.getPassword() == null || dto.getPassword().isEmpty())
            throw new IllegalArgumentException("Password required");

        if (!dto.getPassword().equals(dto.getConfirmPassword()))
            throw new IllegalArgumentException("Passwords do not match");

        Role role = dto.getRole() == null ? Role.EMPLOYEE : dto.getRole();

        // Create USER record
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setCreatedAt(java.time.LocalDateTime.now());

        User savedUser = repo.save(user);

        // Create EMPLOYEE record (for HR/Admin to manage employees)
        try {
            Employee emp = new Employee();
            emp.setName(dto.getName());
            emp.setEmail(email);
            emp.setDepartment(dto.getDepartment());
            emp.setJobTitle(dto.getJobTitle());
            emp.setPhone(dto.getPhone());

            if (dto.getDateOfJoining() != null && !dto.getDateOfJoining().isBlank()) {
                try {
                    emp.setDateOfJoining(LocalDate.parse(dto.getDateOfJoining()));
                } catch (DateTimeParseException ignored) {}
            }

            emp.setStatus(dto.getStatus() == null ? "ACTIVE" : dto.getStatus());

            employeeRepo.save(emp);

        } catch (Exception ex) {
            System.err.println("Warning: Could not create employee row for " + email);
        }

        return savedUser;
    }

    // -------------------------------------------------------
    // ✔ FIND USER BY EMAIL
    // -------------------------------------------------------
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    // -------------------------------------------------------
    // ✔ UPDATE EMAIL
    // -------------------------------------------------------
    public void updateEmail(String currentEmail, String newEmail) {
        User u = repo.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (repo.existsByEmail(newEmail))
            throw new IllegalArgumentException("Email already in use");

        u.setEmail(newEmail);
        repo.save(u);
    }

    // -------------------------------------------------------
    // ✔ CHANGE PASSWORD
    // -------------------------------------------------------
    public void changePassword(String email, String oldPass, String newPass) {

        User u = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPass, u.getPassword()))
            throw new IllegalArgumentException("Old password incorrect");

        u.setPassword(passwordEncoder.encode(newPass));
        repo.save(u);
    }

    // -------------------------------------------------------
    // ✔ REQUIRED BY SPRING SECURITY
    // -------------------------------------------------------
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User u = repo.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + u.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                List.of(authority)
        );
    }
}
