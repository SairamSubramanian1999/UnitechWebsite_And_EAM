package com.unitech.employee.service;

import com.unitech.employee.model.Attendance;
import com.unitech.employee.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AttendanceService {

    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo) {
        this.repo = repo;
    }

    public List<Attendance> getUserAttendance(Long userId) {
        return repo.findAllByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Attendance::getDate).reversed())
                .collect(Collectors.toList());
    }

    public Attendance checkIn(Long userId) {
        LocalDate today = LocalDate.now();

        Attendance att = repo.findByUserIdAndDate(userId, today)
                .orElse(new Attendance(userId, today));

        if (att.getCheckIn() == null) {
            att.setCheckIn(LocalDateTime.now());
        }

        return repo.save(att);
    }

    public Attendance checkOut(Long userId) {
        LocalDate today = LocalDate.now();

        Attendance att = repo.findByUserIdAndDate(userId, today)
                .orElse(new Attendance(userId, today));

        if (att.getCheckOut() == null) {
            att.setCheckOut(LocalDateTime.now());
        }

        return repo.save(att);
    }

    /**
     * Build a monthly report for the given user and YearMonth.
     *
     * Returns a Map with:
     *  - "days": List of day objects { date, checkIn, checkOut, durationHours (double or null) }
     *  - "summary": { totalDaysPresent, totalHours, avgHoursPerDay, firstCheckIn, lateCount, missingDays, month, year }
     */
    public Map<String, Object> getMonthlyReport(Long userId, YearMonth ym) {
        // fetch all attendance for the month (we have findAllByUserId; filter in memory)
        List<Attendance> all = repo.findAllByUserId(userId);
        Map<LocalDate, Attendance> map = all.stream()
                .filter(a -> a.getDate() != null && YearMonth.from(a.getDate()).equals(ym))
                .collect(Collectors.toMap(Attendance::getDate, a -> a, (a, b) -> a));

        int daysInMonth = ym.lengthOfMonth();

        List<Map<String, Object>> days = new ArrayList<>(daysInMonth);

        double totalHours = 0.0;
        int presentDays = 0;
        List<LocalDateTime> firstCheckIns = new ArrayList<>();
        int lateCount = 0;
        // define late threshold (e.g., after 09:30)
        LocalTime lateThreshold = LocalTime.of(9, 30);

        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = ym.atDay(d);
            Attendance a = map.get(date);
            Map<String, Object> day = new HashMap<>();
            day.put("date", date.toString());
            if (a != null && (a.getCheckIn() != null || a.getCheckOut() != null)) {
                String in = a.getCheckIn() == null ? null : a.getCheckIn().toString();
                String out = a.getCheckOut() == null ? null : a.getCheckOut().toString();
                day.put("checkIn", in);
                day.put("checkOut", out);
                Double durationHours = null;
                if (a.getCheckIn() != null && a.getCheckOut() != null) {
                    long seconds = ChronoUnit.SECONDS.between(a.getCheckIn(), a.getCheckOut());
                    durationHours = seconds / 3600.0;
                    totalHours += durationHours;
                    presentDays++;
                }
                day.put("durationHours", durationHours == null ? null : Math.round(durationHours * 100.0) / 100.0);
                if (a.getCheckIn() != null) {
                    firstCheckIns.add(a.getCheckIn());
                    if (a.getCheckIn().toLocalTime().isAfter(lateThreshold)) lateCount++;
                }
            } else {
                day.put("checkIn", null);
                day.put("checkOut", null);
                day.put("durationHours", null);
            }
            days.add(day);
        }

        double avg = presentDays == 0 ? 0.0 : totalHours / (double) presentDays;

        // earliest firstCheckIn for the month
        String firstCheckInStr = null;
        if (!firstCheckIns.isEmpty()) {
            firstCheckInStr = firstCheckIns.stream().min(LocalDateTime::compareTo).get().toString();
        }

        int missingDays = daysInMonth - presentDays;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDaysPresent", presentDays);
        summary.put("totalHours", Math.round(totalHours * 100.0) / 100.0);
        summary.put("avgHoursPerDay", Math.round(avg * 100.0) / 100.0);
        summary.put("firstCheckIn", firstCheckInStr);
        summary.put("lateCount", lateCount);
        summary.put("missingDays", missingDays);
        summary.put("year", ym.getYear());
        summary.put("month", ym.getMonthValue());

        Map<String, Object> result = new HashMap<>();
        result.put("days", days);
        result.put("summary", summary);

        return result;
    }
}
