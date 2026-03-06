// File: src/main/java/com/unitech/employee/repository/LeaveRepository.java
package com.unitech.employee.repository;

import com.unitech.employee.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findAllByUserId(Long userId);
    List<Leave> findAllByStatus(Leave.LeaveStatus status);
}
