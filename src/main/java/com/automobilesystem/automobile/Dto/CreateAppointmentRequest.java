package com.automobilesystem.automobile.Dto;

import java.time.LocalDate;

// UPDATED: Added appointmentDate and service type parameters
// This DTO is used when creating a new appointment from the frontend
public record CreateAppointmentRequest(
        String clientId,
        String clientName,
        String employeeId,
        String employeeName,
        String description,
        // ADDED: The date when the appointment is scheduled
        // Frontend sends this in YYYY-MM-DD format (e.g., "2025-11-10")
        LocalDate appointmentDate,
        // ADDED: Service type information
        // These fields store which service package was selected (Basic, Standard, Premium, etc.)
        String serviceTypeId,      // e.g., "basic", "standard", "premium"
        String serviceTypeName     // e.g., "Basic Service", "Standard Service"
) {

}
