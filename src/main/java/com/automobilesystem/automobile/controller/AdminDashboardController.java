package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Dto.AdminDashboardDtos.*;
import com.automobilesystem.automobile.Dto.ClerkUserDto;
import com.automobilesystem.automobile.Service.AdminDashboardService;
import com.automobilesystem.automobile.Service.AdminDashboardWebSocketService;
import com.automobilesystem.automobile.Service.ClerkService;
import com.automobilesystem.automobile.model.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;
    
    @Autowired
    private ClerkService clerkService;
    
    @Autowired
    private AdminDashboardWebSocketService webSocketService;

    /**
     * Get appointments by date with optional status filter
     * GET /api/admin/appointments?date=2025-11-08&status=PENDING
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<AppointmentDto> appointments = adminDashboardService.getAppointmentsByDate(date, status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all appointments with optional status filter
     * GET /api/admin/appointments/all?status=PENDING
     */
    @GetMapping("/appointments/all")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<AppointmentDto> appointments = adminDashboardService.getAllAppointments(status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get appointment statistics
     * GET /api/admin/appointments/stats
     */
    @GetMapping("/appointments/stats")
    public ResponseEntity<AppointmentStatsDto> getAppointmentStats() {
        try {
            AppointmentStatsDto stats = adminDashboardService.getAppointmentStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get appointment statistics for a specific date
     * GET /api/admin/appointments/stats?date=2025-11-08
     */
    @GetMapping("/appointments/stats/date")
    public ResponseEntity<AppointmentStatsDto> getAppointmentStatsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            AppointmentStatsDto stats = adminDashboardService.getAppointmentStatsByDate(date);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all employees
     * GET /api/admin/employees
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        try {
            List<EmployeeDto> employees = adminDashboardService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all clients/users from Clerk
     * GET /api/admin/clients
     */
    @GetMapping("/clients")
    public ResponseEntity<List<ClerkUserDto>> getAllClients() {
        try {
            List<ClerkUserDto> clients = clerkService.getAllUsers();
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Assign employee to appointment
     * PATCH /api/admin/appointments/{appointmentId}/assign
     */
    @PatchMapping("/appointments/{appointmentId}/assign")
    public ResponseEntity<AppointmentDto> assignEmployeeToAppointment(
            @PathVariable String appointmentId,
            @RequestBody AssignEmployeeRequest request) {
        try {
            AppointmentDto updatedAppointment = adminDashboardService.assignEmployeeToAppointment(
                    appointmentId, request.getEmployeeId());
            
            // Send real-time update via WebSocket
            webSocketService.sendAppointmentAssignment(updatedAppointment);
            webSocketService.sendEmployeeUpdate(adminDashboardService.getAllEmployees());
            
            return ResponseEntity.ok(updatedAppointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get tasks for a specific employee
     * GET /api/admin/employees/{employeeId}/tasks
     */
    @GetMapping("/employees/{employeeId}/tasks")
    public ResponseEntity<List<AppointmentDto>> getEmployeeTasks(@PathVariable String employeeId) {
        try {
            List<AppointmentDto> tasks = adminDashboardService.getEmployeeTasks(employeeId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update appointment status
     * PATCH /api/admin/appointments/{appointmentId}/status
     */
    @PatchMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @RequestBody UpdateStatusRequest request) {
        try {
            AppointmentDto updatedAppointment = adminDashboardService.updateAppointmentStatus(
                    appointmentId, request.getStatus());
            
            // Send real-time update via WebSocket
            webSocketService.sendAppointmentStatusChange(updatedAppointment);
            webSocketService.sendStatsUpdate(adminDashboardService.getAppointmentStats());
            
            return ResponseEntity.ok(updatedAppointment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all clients from Clerk
     * GET /api/admin/clerk/clients
     */
    @GetMapping("/clerk/clients")
    public ResponseEntity<List<ClerkUserDto>> getClerkClients() {
        try {
            List<ClerkUserDto> clients = clerkService.getUsersByRole("client");
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            System.err.println("Error fetching clients from Clerk: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all users from Clerk
     * GET /api/admin/clerk/users
     */
    @GetMapping("/clerk/users")
    public ResponseEntity<List<ClerkUserDto>> getAllClerkUsers() {
        try {
            List<ClerkUserDto> users = clerkService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error fetching users from Clerk: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user statistics from Clerk
     * GET /api/admin/clerk/stats
     */
    @GetMapping("/clerk/stats")
    public ResponseEntity<Map<String, Object>> getClerkStats() {
        try {
            Map<String, Object> stats = clerkService.getClientStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error fetching stats from Clerk: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Refresh and broadcast Clerk data to all admin clients
     * POST /api/admin/clerk/refresh
     */
    @PostMapping("/clerk/refresh")
    public ResponseEntity<String> refreshClerkData() {
        try {
            // Fetch latest Clerk data
            List<ClerkUserDto> users = clerkService.getAllUsers();
            Map<String, Object> stats = clerkService.getClientStats();
            
            // Send real-time updates via WebSocket
            webSocketService.sendClerkUsersUpdate(users);
            webSocketService.sendClerkStatsUpdate(stats);
            
            return ResponseEntity.ok("Clerk data refreshed and broadcasted successfully");
        } catch (Exception e) {
            System.err.println("Error refreshing Clerk data: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error refreshing Clerk data: " + e.getMessage());
        }
    }

    /**
     * Trigger full dashboard refresh for all connected admin clients
     * POST /api/admin/refresh-all
     */
    @PostMapping("/refresh-all")
    public ResponseEntity<String> refreshAllDashboardData() {
        try {
            // Send general refresh signal
            webSocketService.sendDashboardRefresh();
            
            // Also send latest stats
            AppointmentStatsDto stats = adminDashboardService.getAppointmentStats();
            List<EmployeeDto> employees = adminDashboardService.getAllEmployees();
            
            webSocketService.sendStatsUpdate(stats);
            webSocketService.sendEmployeeUpdate(employees);
            
            return ResponseEntity.ok("Dashboard refresh signal sent to all admin clients");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error sending refresh signal: " + e.getMessage());
        }
    }

    /**
     * Create sample data for testing - Development only
     * POST /api/admin/debug/create-sample-data
     */
    @PostMapping("/debug/create-sample-data")
    public ResponseEntity<String> createSampleData() {
        try {
            adminDashboardService.createSampleData();
            return ResponseEntity.ok("Sample data created successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating sample data: " + e.getMessage());
        }
    }

    @GetMapping("/api/all-employees")

    public List<ClerkUserDto> getAllEmployyes() throws  Exception{


         return clerkService.getAllUsers().stream().filter( user -> user.role().equals("employee")).toList();
    }
}
