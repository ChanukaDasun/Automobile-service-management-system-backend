package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.model.Appointment;
import com.automobilesystem.automobile.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*") // allow React frontend to call backend
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Create appointment
    @PostMapping
    public String createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    // Get all appointments
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // Get appointments by date (example: /api/appointments/date/2025-11-10)
    @GetMapping("/date/{date}")
    public List<Appointment> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAppointmentsByDate(date);
    }

    // Assign employee to appointment
    @PutMapping("/{id}/assign/{employeeId}")
    public Appointment assignEmployee(
            @PathVariable String id,
            @PathVariable String employeeId) {
        return appointmentService.assignEmployee(id, employeeId);
    }
}
