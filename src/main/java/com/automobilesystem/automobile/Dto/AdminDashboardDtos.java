package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;
import com.automobilesystem.automobile.model.VehicleType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTOs for Admin Dashboard functionality
 */
public class AdminDashboardDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentDto {
        private String id;
        private String clientId;
        private String clientName;
        private VehicleType  vehicleType;
        private LocalDate appointmentDate;
        private String timeSlot;
        private AppointmentStatus status;
        private String assignedEmployeeId;
        private String assignedEmployeeName;
        private String createdAt;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeDto {
        private String id;
        private String name;
        private String email;
        private boolean availability;
        private int assignedAppointments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentStatsDto {
        private long total;
        private long pending;
        private long assigned;
        private long inProgress;
        private long completed;
        private long cancelled;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignEmployeeRequest {
        private String employeeId;  // Only field needed - appointmentId comes from URL path
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        private AppointmentStatus status;
    }
}