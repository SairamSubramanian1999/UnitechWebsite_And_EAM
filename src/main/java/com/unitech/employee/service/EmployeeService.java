package com.unitech.employee.service;

import com.unitech.employee.model.Employee;
import com.unitech.employee.repository.EmployeeRepository;
import com.unitech.employee.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repo;
    private final UserRepository userRepo;

    public EmployeeService(EmployeeRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public List<Employee> listAll() {
        return repo.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Employee create(Employee e) {

        if (e.getEmail() != null && repo.findByEmail(e.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        // GENERATE EMPLOYEE CODE
        Optional<Employee> lastEmployee = repo.findTopByOrderByIdDesc();

        int nextNumber = 1;

        if (lastEmployee.isPresent()) {
            nextNumber = lastEmployee.get().getId().intValue() + 1;
        }

        String employeeCode = "EMP" + String.format("%03d", nextNumber);

        e.setEmployeeCode(employeeCode);

        return repo.save(e);
    }

    @Transactional
    public Employee update(Long id, Employee update) {

        Employee existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        existing.setName(update.getName());
        existing.setEmail(update.getEmail());
        existing.setDepartment(update.getDepartment());
        existing.setJobTitle(update.getJobTitle());
        existing.setPhone(update.getPhone());
        existing.setDateOfJoining(update.getDateOfJoining());
        existing.setStatus(update.getStatus());

        return repo.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {

        Employee emp = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        String email = emp.getEmail();

        repo.delete(emp);

        if (email != null) {
            userRepo.deleteByEmail(email);
        }
    }
}