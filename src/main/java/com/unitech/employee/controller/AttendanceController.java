package com.unitech.employee.controller;

import com.unitech.employee.model.Attendance;
import com.unitech.employee.service.AttendanceService;
import com.unitech.employee.service.UserService;
import com.unitech.employee.model.User;
import com.unitech.employee.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceController(AttendanceService attendanceService, UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    /**
     * Get current authenticated user's attendance (all records).
     */
    @GetMapping
    public List<Attendance> getMyAttendance(Authentication auth) {
        User me = userService.findByEmail(auth.getName());
        return attendanceService.getUserAttendance(me.getId());
    }

    /**
     * GET /api/attendance/month?year=2025&month=11 -> my monthly attendance summary (day list)
     */
    @GetMapping("/month")
    public Map<String, Object> myAttendanceMonth(@RequestParam int year,
                                                 @RequestParam int month,
                                                 Authentication auth) {
        User me = userService.findByEmail(auth.getName());
        YearMonth ym = YearMonth.of(year, month);
        return attendanceService.getMonthlyReport(me.getId(), ym);
    }

    /**
     * Check-in current user
     */
    @PostMapping("/check-in")
    public Attendance checkIn(Authentication auth) {
        Long userId = userService.findByEmail(auth.getName()).getId();
        return attendanceService.checkIn(userId);
    }

    /**
     * Check-out current user
     */
    @PostMapping("/check-out")
    public Attendance checkOut(Authentication auth) {
        Long userId = userService.findByEmail(auth.getName()).getId();
        return attendanceService.checkOut(userId);
    }

    /**
     * Admin/HR: get attendance list for a specific user (all).
     * Only ADMIN or HR may call for other users.
     */
    @GetMapping("/user/{id}")
    public List<Attendance> getUserAttendance(@PathVariable("id") Long id, Authentication auth) {
        User me = userService.findByEmail(auth.getName());
        if (!me.getId().equals(id) && !(me.getRole() == Role.HR || me.getRole() == Role.ADMIN)) {
            throw new RuntimeException("Forbidden");
        }
        return attendanceService.getUserAttendance(id);
    }

    /**
     * Admin/HR: get monthly report for a specific user
     * GET /api/attendance/user/{id}/month?year=2025&month=11
     */
    @GetMapping("/user/{id}/month")
    public Map<String, Object> getUserMonth(@PathVariable("id") Long id,
                                            @RequestParam int year,
                                            @RequestParam int month,
                                            Authentication auth) {
        User me = userService.findByEmail(auth.getName());
        if (!me.getId().equals(id) && !(me.getRole() == Role.HR || me.getRole() == Role.ADMIN)) {
            throw new RuntimeException("Forbidden");
        }
        return attendanceService.getMonthlyReport(id, YearMonth.of(year, month));
    }
}
