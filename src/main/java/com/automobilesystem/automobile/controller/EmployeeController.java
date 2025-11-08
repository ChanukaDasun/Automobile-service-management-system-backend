package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Dto.EmployeeDashboardDtos.*;
import com.automobilesystem.automobile.Service.EmployeeService;
import com.automobilesystem.automobile.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for employee dashboard functionality - new addition without modifying existing controllers
 */
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Get task statistics for an employee
     * GET /api/employee/tasks/statistics?employeeId=emp123
     */
    @GetMapping("/tasks/statistics")
    public ResponseEntity<EmployeeTaskStatistics> getTaskStatistics(@RequestParam String employeeId) {
        try {
            EmployeeTaskStatistics statistics = employeeService.getEmployeeStatistics(employeeId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get today's tasks for an employee  
     * GET /api/employee/tasks/today?employeeId=emp123
     */
    @GetMapping("/tasks/today")
    public ResponseEntity<List<EmployeeTaskDto>> getTodayTasks(@RequestParam String employeeId) {
        try {
            List<EmployeeTaskDto> todayTasks = employeeService.getTodayTasks(employeeId);
            return ResponseEntity.ok(todayTasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all employee tasks with optional filtering
     * GET /api/employee/tasks?employeeId=emp123&date=2025-11-07&status=PENDING
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<EmployeeTaskDto>> getEmployeeTasks(
            @RequestParam String employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        try {
            List<EmployeeTaskDto> tasks = employeeService.getEmployeeTasks(employeeId, date, status);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get past tasks for an employee
     * GET /api/employee/tasks/past?employeeId=emp123
     */
    @GetMapping("/tasks/past")
    public ResponseEntity<List<EmployeeTaskDto>> getPastTasks(@RequestParam String employeeId) {
        try {
            List<EmployeeTaskDto> pastTasks = employeeService.getPastTasks(employeeId);
            return ResponseEntity.ok(pastTasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update task status
     * PATCH /api/employee/tasks/{id}/status?employeeId=emp123
     */
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<EmployeeTaskDto> updateTaskStatus(
            @PathVariable String id,
            @RequestParam String employeeId,
            @RequestBody UpdateTaskStatusRequest request) {
        try {
            EmployeeTaskDto updatedTask = employeeService.updateTaskStatus(id, employeeId, request);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Set estimated completion time for a task
     * PATCH /api/employee/tasks/{id}/estimated-completion?employeeId=emp123
     */
    @PatchMapping("/tasks/{id}/estimated-completion")
    public ResponseEntity<EmployeeTaskDto> setEstimatedCompletion(
            @PathVariable String id,
            @RequestParam String employeeId,
            @RequestBody SetEstimatedCompletionRequest request) {
        try {
            EmployeeTaskDto updatedTask = employeeService.setEstimatedCompletion(id, employeeId, request);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get filtered tasks using the new filter request
     * POST /api/employee/tasks/filter
     */
    @PostMapping("/tasks/filter")
    public ResponseEntity<List<EmployeeTaskDto>> getFilteredTasks(@RequestBody EmployeeTaskFilterRequest filter) {
        try {
            List<EmployeeTaskDto> filteredTasks = employeeService.getFilteredTasks(filter);
            return ResponseEntity.ok(filteredTasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}