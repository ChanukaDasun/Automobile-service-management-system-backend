package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Dto.AdminDashboardDtos.*;
import com.automobilesystem.automobile.Dto.ClerkUserDto;
import com.automobilesystem.automobile.Service.AdminDashboardService;
import com.automobilesystem.automobile.Service.ClerkService;
import com.automobilesystem.automobile.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final ClerkService clerkService;

    /**
     * Get appointments for a specific date
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<AppointmentDto> appointments = adminDashboardService.getAppointmentsByDate(date, status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Error fetching appointments by date: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all appointments with optional status filter
     */
    @GetMapping("/appointments/all")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<AppointmentDto> appointments = adminDashboardService.getAllAppointments(status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Error fetching all appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get appointment statistics
     */
    @GetMapping("/appointments/stats")
    public ResponseEntity<AppointmentStatsDto> getAppointmentStats() {
        try {
            AppointmentStatsDto stats = adminDashboardService.getAppointmentStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error fetching appointment stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get appointment statistics for a specific date
     */
    @GetMapping("/appointments/stats/{date}")
    public ResponseEntity<AppointmentStatsDto> getAppointmentStatsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            AppointmentStatsDto stats = adminDashboardService.getAppointmentStatsByDate(date);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error fetching appointment stats by date: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all employees from database
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        try {
            List<EmployeeDto> employees = adminDashboardService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            System.err.println("Error fetching employees from database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all employees from Clerk - FIXED VERSION
     */
    @GetMapping("/all-employees")
    public ResponseEntity<List<ClerkUserDto>> getAllEmployeesFromClerk() {
        try {
            System.out.println("Fetching all users from Clerk...");

            // Get all users from Clerk
            List<ClerkUserDto> allUsers = clerkService.getAllUsers();
            System.out.println("Total users from Clerk: " + allUsers.size());

            // Filter for employees only (users with role = "employee")
            List<ClerkUserDto> employees = allUsers.stream()
                    .filter(user -> "employee".equals(user.role()))
                    .collect(Collectors.toList());

            System.out.println("Filtered employees: " + employees.size());

            // If no specific employees found, return all users for now
            if (employees.isEmpty() && !allUsers.isEmpty()) {
                System.out.println("No users with 'employee' role found, returning all users for assignment");
                return ResponseEntity.ok(allUsers);
            }

            return ResponseEntity.ok(employees);

        } catch (Exception e) {
            System.err.println("Error fetching employees from Clerk: " + e.getMessage());
            e.printStackTrace();
            // Return empty list to prevent frontend from breaking
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get users by role - Additional endpoint for role-based filtering
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<ClerkUserDto>> getUsersByRole(@PathVariable String role) {
        try {
            List<ClerkUserDto> users = clerkService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error fetching users by role: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get client statistics - Additional endpoint for dashboard stats
     */
    @GetMapping("/users/stats")
    public ResponseEntity<java.util.Map<String, Object>> getClientStats() {
        try {
            java.util.Map<String, Object> stats = clerkService.getClientStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error fetching client stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Assign employee to appointment
     */
    @PatchMapping("/appointments/{appointmentId}/assign")
    public ResponseEntity<AppointmentDto> assignEmployeeToAppointment(
            @PathVariable String appointmentId,
            @RequestBody AssignEmployeeRequest request) {
        try {
            System.out.println("=== Assignment Controller ===");
            System.out.println("Appointment ID: " + appointmentId);
            System.out.println("Employee ID from request: " + request.getEmployeeId());

            if (request.getEmployeeId() == null || request.getEmployeeId().trim().isEmpty()) {
                System.err.println("Empty employee ID in request");
                return ResponseEntity.badRequest().build();
            }

            AppointmentDto updatedAppointment = adminDashboardService.assignEmployeeToAppointment(
                    appointmentId,
                    request.getEmployeeId()
            );

            System.out.println("✅ Assignment successful in controller");
            return ResponseEntity.ok(updatedAppointment);

        } catch (IllegalArgumentException e) {
            System.err.println("Bad request in assignment: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            System.err.println("Assignment error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Unexpected error in assignment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update appointment status
     */
    @PatchMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @RequestBody UpdateStatusRequest request) {
        try {
            if (request.getStatus() == null) {
                return ResponseEntity.badRequest().build();
            }

            AppointmentDto updatedAppointment = adminDashboardService.updateAppointmentStatus(
                    appointmentId,
                    request.getStatus()
            );

            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get employee tasks
     */
    @GetMapping("/employees/{employeeId}/tasks")
    public ResponseEntity<List<AppointmentDto>> getEmployeeTasks(@PathVariable String employeeId) {
        try {
            List<AppointmentDto> tasks = adminDashboardService.getEmployeeTasks(employeeId);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error fetching employee tasks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Unassign employee from appointment
     */
    @PatchMapping("/appointments/{appointmentId}/unassign")
    public ResponseEntity<AppointmentDto> unassignEmployeeFromAppointment(@PathVariable String appointmentId) {
        try {
            AppointmentDto updatedAppointment = adminDashboardService.unassignEmployeeFromAppointment(appointmentId);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Error unassigning employee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get unassigned appointments
     */
    @GetMapping("/appointments/unassigned")
    public ResponseEntity<List<AppointmentDto>> getUnassignedAppointments() {
        try {
            List<AppointmentDto> appointments = adminDashboardService.getAllAppointments(AppointmentStatus.PENDING);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Error fetching unassigned appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get available employees
     */
    @GetMapping("/employees/available")
    public ResponseEntity<List<EmployeeDto>> getAvailableEmployees() {
        try {
            List<EmployeeDto> allEmployees = adminDashboardService.getAllEmployees();
            List<EmployeeDto> availableEmployees = allEmployees.stream()
                    .filter(EmployeeDto::isAvailability)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(availableEmployees);
        } catch (Exception e) {
            System.err.println("Error fetching available employees: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}