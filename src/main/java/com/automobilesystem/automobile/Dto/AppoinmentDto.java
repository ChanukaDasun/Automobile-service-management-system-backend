package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// UPDATED: Added appointmentDate and service type fields to the DTO
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
        LocalDate appointmentDate,
        // ADDED: Service type information
        String serviceTypeId,      // ID of the selected service type
        String serviceTypeName     // Name of the selected service type
) {
}
