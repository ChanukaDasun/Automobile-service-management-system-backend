package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Dto.EmployeeDashboardDtos.*;
import static com.automobilesystem.automobile.Dto.EmployeeDashboardDtos.Converter;
import com.automobilesystem.automobile.Repository.AppoinmentRepo;
import com.automobilesystem.automobile.model.Appoinment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for employee dashboard functionality - new addition without modifying existing services
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final AppoinmentRepo appoinmentRepo;

    /**
     * Get task statistics for an employee
     */
    public EmployeeTaskStatistics getEmployeeStatistics(String employeeId) {
        List<Appoinment> allTasks = appoinmentRepo.findByEmployeeId(employeeId);
        
        List<Appoinment> filteredTasks = allTasks.stream()
                .filter(t -> t.getStatus() != AppointmentStatus.CANCELLED) // Exclude cancelled appointments from employee view
                .collect(Collectors.toList());
        LocalDate today = LocalDate.now();
        
        int total = filteredTasks.size();
        int assigned = (int) filteredTasks.stream().filter(t -> t.getStatus() == AppointmentStatus.ASSIGNED).count();
        int inProgress = (int) filteredTasks.stream().filter(t -> t.getStatus() == AppointmentStatus.IN_PROGRESS).count();
        int completed = (int) filteredTasks.stream().filter(t -> t.getStatus() == AppointmentStatus.COMPLETED).count();
        
        List<Appoinment> todayTasks = filterActiveTasks(appoinmentRepo.findTodayTasksByEmployee(employeeId, today));
        List<Appoinment> pastTasks = filterActiveTasks(appoinmentRepo.findPastTasksByEmployee(employeeId, today));
        
        return new EmployeeTaskStatistics(
            total,
            assigned,
            inProgress,
            completed,
            todayTasks.size(),
            pastTasks.size()
        );
    }

    /**
     * Get today's tasks for an employee (excludes cancelled tasks)
     */
    public List<EmployeeTaskDto> getTodayTasks(String employeeId) {
        LocalDate today = LocalDate.now();
        List<Appoinment> todayTasks = appoinmentRepo.findTodayTasksByEmployee(employeeId, today);
        return filterActiveTasks(todayTasks).stream()
                .map(this::convertToEmployeeTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for an employee with optional filtering
     */
    public List<EmployeeTaskDto> getEmployeeTasks(String employeeId, LocalDate date, AppointmentStatus status) {
        List<Appoinment> tasks;
        
        if (date != null && status != null) {
            // Filter by both date and status
            tasks = appoinmentRepo.findByEmployeeIdAndAppointmentDate(employeeId, date)
                    .stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        } else if (date != null) {
            // Filter by date only
            tasks = appoinmentRepo.findByEmployeeIdAndAppointmentDate(employeeId, date);
        } else if (status != null) {
            // Filter by status only (but exclude cancelled if not specifically requested)
            if (status == AppointmentStatus.CANCELLED) {
                tasks = List.of(); // Employees cannot see cancelled tasks
            } else {
                tasks = appoinmentRepo.findByEmployeeIdAndStatus(employeeId, status);
            }
        } else {
            // No filtering
            tasks = appoinmentRepo.findByEmployeeId(employeeId);
        }
        
        List<Appoinment> activeTasks = filterActiveTasks(tasks);
        
        List<EmployeeTaskDto> result = activeTasks.stream()
                .map(this::convertToEmployeeTaskDto)
                .collect(Collectors.toList());
        
        return result;
    }

    /**
     * Get past tasks for an employee (excludes cancelled tasks)
     */
    public List<EmployeeTaskDto> getPastTasks(String employeeId) {
        LocalDate today = LocalDate.now();
        List<Appoinment> pastTasks = appoinmentRepo.findPastTasksByEmployee(employeeId, today);
        return filterActiveTasks(pastTasks).stream()
                .map(this::convertToEmployeeTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * Update task status safely - only if employee is assigned to the task
     */
    public EmployeeTaskDto updateTaskStatus(String appointmentId, String employeeId, UpdateTaskStatusRequest request) {
        Appoinment appointment = appoinmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Security check - ensure employee can only update their own tasks
        if (!appointment.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized: You are not assigned to this appointment");
        }
        
        appointment.setStatus(request.status());
        appointment.setStatusMessage(request.statusMessage());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appoinment updated = appoinmentRepo.save(appointment);
        return convertToEmployeeTaskDto(updated);
    }

    /**
     * Set estimated completion time for a task
     */
    public EmployeeTaskDto setEstimatedCompletion(String appointmentId, String employeeId, SetEstimatedCompletionRequest request) {
        Appoinment appointment = appoinmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Security check - ensure employee can only update their own tasks
        if (!appointment.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized: You are not assigned to this appointment");
        }
        
        appointment.setEstimatedCompletion(request.estimatedCompletion());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appoinment updated = appoinmentRepo.save(appointment);
        return convertToEmployeeTaskDto(updated);
    }

    /**
     * Convert Appointment to EmployeeTaskDto using the consolidated converter
     */
    private EmployeeTaskDto convertToEmployeeTaskDto(Appoinment appointment) {
        return Converter.toEmployeeTaskDto(appointment);
    }

    /**
     * Filter out cancelled appointments - employees should not see cancelled tasks
     */
    private List<Appoinment> filterActiveTasks(List<Appoinment> appointments) {
        return appointments.stream()
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * Get filtered tasks using the new EmployeeTaskFilterRequest
     */
    public List<EmployeeTaskDto> getFilteredTasks(EmployeeTaskFilterRequest filter) {
        LocalDate today = LocalDate.now();
        List<Appoinment> tasks = appoinmentRepo.findByEmployeeId(filter.employeeId());
        
        // Apply filters
        List<Appoinment> filteredTasks = tasks.stream()
                .filter(t -> t.getStatus() != AppointmentStatus.CANCELLED) // Always exclude cancelled
                .filter(t -> {
                    // Status filter
                    if (filter.status() != null) {
                        return t.getStatus() == filter.status();
                    }
                    return true;
                })
                .filter(t -> {
                    // Date-based filters
                    LocalDate taskDate = t.getAppointmentDate();
                    if (taskDate == null) return true;
                    
                    boolean include = false;
                    
                    if (filter.includeToday() && taskDate.equals(today)) {
                        include = true;
                    }
                    if (filter.includePast() && taskDate.isBefore(today)) {
                        include = true;
                    }
                    if (filter.includeUpcoming() && taskDate.isAfter(today)) {
                        include = true;
                    }
                    
                    // If specific date is provided, check that too
                    if (filter.date() != null && taskDate.equals(filter.date())) {
                        include = true;
                    }
                    
                    return include;
                })
                .collect(Collectors.toList());
        
        return filteredTasks.stream()
                .map(this::convertToEmployeeTaskDto)
                .collect(Collectors.toList());
    }
}