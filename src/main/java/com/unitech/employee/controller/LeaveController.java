// File: src/main/java/com/unitech/employee/controller/LeaveController.java
package com.unitech.employee.controller;

import com.unitech.employee.model.Leave;
import com.unitech.employee.model.User;
import com.unitech.employee.service.LeaveService;
import com.unitech.employee.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;

    public LeaveController(LeaveService leaveService, UserService userService) {
        this.leaveService = leaveService;
        this.userService = userService;
    }

    // Employee: apply
    @PostMapping("/apply")
    public Leave apply(@RequestBody Leave payload, Authentication auth) {
        User u = userService.findByEmail(auth.getName());
        payload.setUserId(u.getId());
        payload.setStatus(Leave.LeaveStatus.PENDING);
        return leaveService.applyLeave(payload);
    }

    // Employee: list own leaves
    @GetMapping("/me")
    public List<Leave> myLeaves(Authentication auth) {
        User u = userService.findByEmail(auth.getName());
        return leaveService.getByUser(u.getId());
    }

    // HR: list pending (secured via SecurityConfig hasRole("HR"))
    @GetMapping("/hr/pending")
    public List<Leave> pendingLeaves() {
        return leaveService.getPending();
    }

    // HR: approve/deny by id
    @PostMapping("/hr/{id}/status")
    public Leave setStatus(@PathVariable Long id, @RequestParam("status") String status) {
        Leave.LeaveStatus s = Leave.LeaveStatus.valueOf(status.toUpperCase());
        return leaveService.updateStatus(id, s);
    }
}
