package com.unitech.employee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDayDto {
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Long durationMinutes;
    private Double durationHours; // nullable when missing data

    public AttendanceDayDto() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public Long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
}
