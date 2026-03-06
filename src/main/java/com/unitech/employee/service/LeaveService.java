// File: src/main/java/com/unitech/employee/service/LeaveService.java
package com.unitech.employee.service;

import com.unitech.employee.model.Leave;
import com.unitech.employee.repository.LeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveService {

    private final LeaveRepository repo;

    public LeaveService(LeaveRepository repo) { this.repo = repo; }

    public Leave applyLeave(Leave l) { return repo.save(l); }

    public List<Leave> getByUser(Long userId) { return repo.findAllByUserId(userId); }

    public List<Leave> getPending() { return repo.findAllByStatus(Leave.LeaveStatus.PENDING); }

    public Optional<Leave> findById(Long id) { return repo.findById(id); }

    public Leave updateStatus(Long id, Leave.LeaveStatus status) {
        Leave l = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave not found"));
        l.setStatus(status);
        return repo.save(l);
    }
}
