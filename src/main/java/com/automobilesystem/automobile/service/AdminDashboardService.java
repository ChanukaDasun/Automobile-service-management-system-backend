package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Dto.AdminDashboardDtos.*;
import com.automobilesystem.automobile.Repository.AppointmentRepository;
import com.automobilesystem.automobile.Repository.EmployeeRepo;
import com.automobilesystem.automobile.model.Appointment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import com.automobilesystem.automobile.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * Get appointments for a specific date with optional status filter
     */
    public List<AppointmentDto> getAppointmentsByDate(LocalDate date, AppointmentStatus status) {
        List<Appointment> appointments;

        if (status != null) {
            appointments = appointmentRepo.findByAppointmentDateAndStatus(date, status);
        } else {
            appointments = appointmentRepo.findByAppointmentDate(date);
        }

        return appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all appointments with optional status filter
     */
    public List<AppointmentDto> getAllAppointments(AppointmentStatus status) {
        List<Appointment> appointments;

        if (status != null) {
            appointments = appointmentRepo.findByStatus(status);
        } else {
            appointments = appointmentRepo.findAll();
        }

        return appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());
    }

    /**
     * Get appointment statistics
     */
    public AppointmentStatsDto getAppointmentStats() {
        long total = appointmentRepo.count();
        long pending = appointmentRepo.countByStatus(AppointmentStatus.PENDING);
        long assigned = appointmentRepo.countByStatus(AppointmentStatus.ASSIGNED);
        long inProgress = appointmentRepo.countByStatus(AppointmentStatus.IN_PROGRESS);
        long completed = appointmentRepo.countByStatus(AppointmentStatus.COMPLETED);
        long cancelled = appointmentRepo.countByStatus(AppointmentStatus.CANCELLED);

        return new AppointmentStatsDto(total, pending, assigned, inProgress, completed, cancelled);
    }

    /**
     * Get appointment statistics for a specific date
     */
    public AppointmentStatsDto getAppointmentStatsByDate(LocalDate date) {
        List<Appointment> dateAppointments = appointmentRepo.findByAppointmentDate(date);

        long total = dateAppointments.size();
        long pending = dateAppointments.stream().mapToLong(apt -> apt.getStatus() == AppointmentStatus.PENDING ? 1 : 0).sum();
        long assigned = dateAppointments.stream().mapToLong(apt -> apt.getStatus() == AppointmentStatus.ASSIGNED ? 1 : 0).sum();
        long inProgress = dateAppointments.stream().mapToLong(apt -> apt.getStatus() == AppointmentStatus.IN_PROGRESS ? 1 : 0).sum();
        long completed = dateAppointments.stream().mapToLong(apt -> apt.getStatus() == AppointmentStatus.COMPLETED ? 1 : 0).sum();
        long cancelled = dateAppointments.stream().mapToLong(apt -> apt.getStatus() == AppointmentStatus.CANCELLED ? 1 : 0).sum();

        return new AppointmentStatsDto(total, pending, assigned, inProgress, completed, cancelled);
    }

    /**
     * Get all employees with their current task counts
     */
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();

        return employees.stream()
                .map(employee -> {
                    try {
                        // Get assigned appointments count from database
                        long assignedCount = appointmentRepo.countByEmployeeId(employee.getEmployeeId());

                        // Create DTO with current values
                        EmployeeDto dto = convertToEmployeeDto(employee);
                        dto.setAssignedAppointments((int) assignedCount);
                        dto.setAvailability(assignedCount <= 3);

                        return dto;
                    } catch (Exception e) {
                        System.err.println("Error processing employee " + employee.getEmployeeId() + ": " + e.getMessage());
                        return convertToEmployeeDto(employee);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Assign an employee to an appointment - PRODUCTION READY
     */
    public AppointmentDto assignEmployeeToAppointment(String appointmentId, String employeeId) {
        System.out.println("=== Assignment Request ===");
        System.out.println("Appointment ID: " + appointmentId);
        System.out.println("Employee ID: " + employeeId);

        try {
            // Validate inputs
            if (appointmentId == null || appointmentId.trim().isEmpty()) {
                throw new IllegalArgumentException("Appointment ID cannot be null or empty");
            }
            if (employeeId == null || employeeId.trim().isEmpty()) {
                throw new IllegalArgumentException("Employee ID cannot be null or empty");
            }

            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId.trim());
            if (appointmentOpt.isEmpty()) {
                throw new RuntimeException("Appointment not found with ID: " + appointmentId);
            }
            Appointment appointment = appointmentOpt.get();
            System.out.println("✅ Found appointment: " + appointment.getId());

            // Find the employee (check both database and Clerk)
            Optional<Employee> employeeOpt = employeeRepo.findByEmployeeId(employeeId.trim());
            if (employeeOpt.isEmpty()) {
                throw new RuntimeException("Employee not found with ID: " + employeeId);
            }
            Employee employee = employeeOpt.get();
            System.out.println("✅ Found employee: " + employee.getName());

            // Update appointment
            appointment.setEmployeeId(employeeId.trim());
            appointment.setEmployeeName(employee.getName());
            appointment.setStatus(AppointmentStatus.ASSIGNED);
            appointment.setUpdatedAt(LocalDateTime.now());

            System.out.println("✅ Updated appointment fields");

            // Save appointment
            Appointment savedAppointment = appointmentRepo.save(appointment);
            System.out.println("✅ Appointment saved successfully");

            // Update employee's assigned appointments count
            long assignedCount = appointmentRepo.countByEmployeeId(employeeId.trim());
            employee.setAssignedAppointments((int) assignedCount);
            employee.setAvailability(assignedCount <= 3);
            employeeRepo.save(employee);

            System.out.println("✅ Assignment completed successfully!");

            return convertToAppointmentDto(savedAppointment);

        } catch (Exception e) {
            System.err.println("❌ Assignment failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Convert Appoinment entity to DTO
     */
    private AppointmentDto convertToAppointmentDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getCustomerId(),
                appointment.getCustomerName(),
                appointment.getVehicleType(),
                appointment.getAppointmentDate(),
                appointment.getTimeSlot(),
                appointment.getStatus(),
                appointment.getEmployeeId(),
                appointment.getEmployeeName(),
                appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null,
                appointment.getDescription()
        );
    }

    /**
     * Convert Employee entity to DTO
     */
    private EmployeeDto convertToEmployeeDto(Employee employee) {
        return new EmployeeDto(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.isAvailability(),
                employee.getAssignedAppointments()
        );
    }

    /**
     * Get all tasks assigned to a specific employee
     */
    public List<AppointmentDto> getEmployeeTasks(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        List<Appointment> employeeAppointments = appointmentRepo.findByEmployeeId(employeeId.trim());

        return employeeAppointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());
    }

    /**
     * Update appointment status
     */
    public AppointmentDto updateAppointmentStatus(String appointmentId, AppointmentStatus status) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID cannot be null or empty");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId.trim());
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found with id: " + appointmentId);
        }

        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());

        // Update employee availability if completing or cancelling
        if (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.CANCELLED) {
            if (appointment.getEmployeeId() != null && !appointment.getEmployeeId().trim().isEmpty()) {
                Optional<Employee> employeeOpt = employeeRepo.findByEmployeeId(appointment.getEmployeeId());
                if (employeeOpt.isPresent()) {
                    Employee employee = employeeOpt.get();
                    long currentTasks = appointmentRepo.countByEmployeeId(employee.getEmployeeId());
                    employee.setAssignedAppointments((int) Math.max(0, currentTasks - 1));
                    employee.setAvailability(employee.getAssignedAppointments() <= 3);
                    employeeRepo.save(employee);
                }
            }
        }

        Appointment savedAppointment = appointmentRepo.save(appointment);
        return convertToAppointmentDto(savedAppointment);
    }

    /**
     * Remove employee assignment from appointment
     */
    public AppointmentDto unassignEmployeeFromAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment ID cannot be null or empty");
        }

        Optional<Appointment> appointmentOpt = appointmentRepo.findById(appointmentId.trim());
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found with id: " + appointmentId);
        }

        Appointment appointment = appointmentOpt.get();
        String previousEmployeeId = appointment.getEmployeeId();

        // Clear employee assignment
        appointment.setEmployeeId(null);
        appointment.setEmployeeName(null);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setUpdatedAt(LocalDateTime.now());

        // Update previous employee's availability
        if (previousEmployeeId != null && !previousEmployeeId.trim().isEmpty()) {
            Optional<Employee> employeeOpt = employeeRepo.findByEmployeeId(previousEmployeeId);
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                long currentTasks = appointmentRepo.countByEmployeeId(employee.getEmployeeId());
                employee.setAssignedAppointments((int) Math.max(0, currentTasks - 1));
                employee.setAvailability(employee.getAssignedAppointments() <= 3);
                employeeRepo.save(employee);
            }
        }

        Appointment savedAppointment = appointmentRepo.save(appointment);
        return convertToAppointmentDto(savedAppointment);
    }
}