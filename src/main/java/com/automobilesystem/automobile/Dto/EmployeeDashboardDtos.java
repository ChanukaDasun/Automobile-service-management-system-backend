package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Consolidated DTOs for Employee Dashboard - All employee-related DTOs in one clean file
 * Created: November 8, 2025
 */
public class EmployeeDashboardDtos {

    /**
     * DTO for employee task statistics
     */
    public record EmployeeTaskStatistics(
        int totalTasks,
        int assignedTasks, 
        int inProgressTasks,
        int completedTasks,
        int todayTasks,
        int pastTasks
    ) {}

    /**
     * Enhanced DTO for employee tasks with all required fields
     */
    public record EmployeeTaskDto(
        String id,
        String clientId,
        String clientName,
        String employeeId,
        String employeeName,
        AppointmentStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String statusMessage,
        // Employee-specific fields
        String vehicleType,
        String timeSlot,
        LocalDate appointmentDate,
        String estimatedCompletion
    ) {}

    /**
     * DTO for updating task status
     */
    public record UpdateTaskStatusRequest(
        AppointmentStatus status,
        String statusMessage
    ) {}

    /**
     * DTO for setting estimated completion time
     */
    public record SetEstimatedCompletionRequest(
        String estimatedCompletion
    ) {}

    /**
     * DTO for task count by status (used in statistics)
     */
    public record TaskStatusCount(
        AppointmentStatus status,
        long count
    ) {}

    /**
     * DTO for employee dashboard response with all data
     */
    public record EmployeeDashboardResponse(
        EmployeeTaskStatistics statistics,
        java.util.List<EmployeeTaskDto> todayTasks,
        java.util.List<EmployeeTaskDto> pastTasks,
        java.util.List<EmployeeTaskDto> upcomingTasks
    ) {}

    /**
     * DTO for employee task filter request
     */
    public record EmployeeTaskFilterRequest(
        String employeeId,
        LocalDate date,
        AppointmentStatus status,
        boolean includePast,
        boolean includeToday,
        boolean includeUpcoming
    ) {}

    /**
     * DTO for employee basic information
     */
    public record EmployeeInfo(
        String employeeId,
        String employeeName,
        String email,
        boolean isActive,
        int currentTaskCount
    ) {}

    /**
     * Utility class for DTO conversions
     */
    public static class Converter {
        
        /**
         * Convert Appointment entity to EmployeeTaskDto
         */
        public static EmployeeTaskDto toEmployeeTaskDto(com.automobilesystem.automobile.model.Appoinment appointment) {
            return new EmployeeTaskDto(
                appointment.getAppoinmentId(),
                appointment.getCustomerId(),
                appointment.getCustomerName(),
                appointment.getEmployeeId(),
                appointment.getEmployeeName(),
                appointment.getStatus(),
                appointment.getDescription(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getStatusMessage(),
                appointment.getVehicleType(),
                appointment.getTimeSlot(),
                appointment.getAppointmentDate(),
                appointment.getEstimatedCompletion()
            );
        }
    }
}