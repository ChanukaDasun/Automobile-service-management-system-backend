package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// UPDATED: Added appointmentDate to the DTO
// This DTO is returned to the frontend when fetching appointment details
public record AppoinmentDto(
        String id,
        String clientId,
        String clientName,
        String employeeId,
        String employeeName,
        AppointmentStatus appointmentStatus,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        String statusMessage,
        // ADDED: The scheduled appointment date
        LocalDate appointmentDate
) {
}
