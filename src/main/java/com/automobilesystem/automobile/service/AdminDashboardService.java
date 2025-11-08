package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Dto.AdminDashboardDtos.*;
import com.automobilesystem.automobile.Repository.AppoinmentRepo;
import com.automobilesystem.automobile.Repository.EmployeeRepo;
import com.automobilesystem.automobile.model.Appoinment;
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
    private AppoinmentRepo appointmentRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * Get appointments for a specific date with optional status filter
     */
    public List<AppointmentDto> getAppointmentsByDate(LocalDate date, AppointmentStatus status) {
        List<Appoinment> appointments;
        
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
        List<Appoinment> appointments;
        
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
        List<Appoinment> dateAppointments = appointmentRepo.findByAppointmentDate(date);
        
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
                        // Return employee with default values if there's an error
                        return convertToEmployeeDto(employee);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Assign an employee to an appointment
     */
    public AppointmentDto assignEmployeeToAppointment(String appointmentId, String employeeId) {
        System.out.println("=== Assignment Debug ===");
        System.out.println("Appointment ID: " + appointmentId);
        System.out.println("Employee ID: " + employeeId);
        
        // Find the appointment
        Appoinment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        System.out.println("Found appointment: " + appointment.getAppoinmentId());

        // Find the employee
        Employee employee = employeeRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        System.out.println("Found employee: " + employee.getName());

        // Update appointment - using correct field names that match the model
        appointment.setEmployeeId(employeeId);  // This should match the field EmployeeId
        appointment.setEmployeeName(employee.getName());  // This should match EmployeeName
        appointment.setStatus(AppointmentStatus.ASSIGNED);
        appointment.setUpdatedAt(LocalDateTime.now());

        // Save appointment
        Appoinment savedAppointment = appointmentRepo.save(appointment);

        // Update employee's assigned appointments count
        long assignedCount = appointmentRepo.countByEmployeeId(employeeId);
        employee.setAssignedAppointments((int) assignedCount);
        employee.setAvailability(assignedCount <= 3); // Update availability
        employeeRepo.save(employee);

        return convertToAppointmentDto(savedAppointment);
    }

    /**
     * Convert Appoinment entity to DTO
     */
    private AppointmentDto convertToAppointmentDto(Appoinment appointment) {
        return new AppointmentDto(
                appointment.getAppoinmentId(),
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
     * Create sample data for testing - call this once to populate database
     */
    public void createSampleData() {
        // Clear existing data
        appointmentRepo.deleteAll();
        employeeRepo.deleteAll();

        // Create sample employees
        Employee emp1 = new Employee("emp001", "Mike Johnson", "mike@example.com", true, 2);
        Employee emp2 = new Employee("emp002", "Sarah Davis", "sarah@example.com", true, 1);
        Employee emp3 = new Employee("emp003", "Tom Wilson", "tom@example.com", true, 0);
        Employee emp4 = new Employee("emp004", "Emma Taylor", "emma@example.com", false, 3);
        
        employeeRepo.saveAll(List.of(emp1, emp2, emp3, emp4));

        // Create sample appointments for today (Nov 8, 2025)
        LocalDate today = LocalDate.of(2025, 11, 8);
        
        Appoinment apt1 = new Appoinment();
        apt1.setAppoinmentId("apt001");
        apt1.setCustomerId("user_123");
        apt1.setCustomerName("John Doe");
        apt1.setVehicleType("Sedan");
        apt1.setAppointmentDate(today);
        apt1.setTimeSlot("09:00 AM");
        apt1.setStatus(AppointmentStatus.PENDING);
        apt1.setDescription("Oil change and filter replacement");
        apt1.setCreatedAt(LocalDateTime.now().minusHours(2));
        apt1.setUpdatedAt(LocalDateTime.now().minusHours(2));

        Appoinment apt2 = new Appoinment();
        apt2.setAppoinmentId("apt002");
        apt2.setCustomerId("user_456");
        apt2.setCustomerName("Jane Smith");
        apt2.setVehicleType("SUV");
        apt2.setAppointmentDate(today);
        apt2.setTimeSlot("10:00 AM");
        apt2.setStatus(AppointmentStatus.ASSIGNED);
        apt2.setEmployeeId("emp001");
        apt2.setEmployeeName("Mike Johnson");
        apt2.setDescription("Brake inspection and service");
        apt2.setCreatedAt(LocalDateTime.now().minusHours(3));
        apt2.setUpdatedAt(LocalDateTime.now().minusHours(1));

        Appoinment apt3 = new Appoinment();
        apt3.setAppoinmentId("apt003");
        apt3.setCustomerId("user_789");
        apt3.setCustomerName("Bob Wilson");
        apt3.setVehicleType("Truck");
        apt3.setAppointmentDate(today);
        apt3.setTimeSlot("11:00 AM");
        apt3.setStatus(AppointmentStatus.PENDING);
        apt3.setDescription("Engine diagnostic check");
        apt3.setCreatedAt(LocalDateTime.now().minusHours(4));
        apt3.setUpdatedAt(LocalDateTime.now().minusHours(4));

        Appoinment apt4 = new Appoinment();
        apt4.setAppoinmentId("apt004");
        apt4.setCustomerId("user_321");
        apt4.setCustomerName("Alice Brown");
        apt4.setVehicleType("Motorcycle");
        apt4.setAppointmentDate(today);
        apt4.setTimeSlot("02:00 PM");
        apt4.setStatus(AppointmentStatus.IN_PROGRESS);
        apt4.setEmployeeId("emp002");
        apt4.setEmployeeName("Sarah Davis");
        apt4.setDescription("Tire replacement and wheel alignment");
        apt4.setCreatedAt(LocalDateTime.now().minusHours(5));
        apt4.setUpdatedAt(LocalDateTime.now().minusMinutes(30));

        appointmentRepo.saveAll(List.of(apt1, apt2, apt3, apt4));
        
        System.out.println("✅ Sample data created for Admin Dashboard");
        System.out.println("   - 4 Employees created");
        System.out.println("   - 4 Appointments created for " + today);
    }

    /**
     * Get all tasks assigned to a specific employee
     */
    public List<AppointmentDto> getEmployeeTasks(String employeeId) {
        List<Appoinment> employeeAppointments = appointmentRepo.findByEmployeeId(employeeId);
        
        return employeeAppointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());
    }

    /**
     * Update appointment status
     */
    public AppointmentDto updateAppointmentStatus(String appointmentId, AppointmentStatus status) {
        Optional<Appoinment> appointmentOpt = appointmentRepo.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found with id: " + appointmentId);
        }

        Appoinment appointment = appointmentOpt.get();
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());

        // Update employee availability if completing or cancelling
        if (status == AppointmentStatus.COMPLETED || status == AppointmentStatus.CANCELLED) {
            if (appointment.getEmployeeId() != null) {
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

        Appoinment savedAppointment = appointmentRepo.save(appointment);
        return convertToAppointmentDto(savedAppointment);
    }
}
