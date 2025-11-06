package com.automobilesystem.automobile.service;

import com.automobilesystem.automobile.model.Appointment;
import com.automobilesystem.automobile.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Create a new appointment (with limit 5 per day per vehicle type)
    public String createAppointment(Appointment appointment) {
        LocalDate date = appointment.getDate();
        String vehicleType = appointment.getVehicleType();

        // count how many appointments exist for same date & vehicle
        List<Appointment> existingAppointments =
                appointmentRepository.findByDate(date)
                        .stream()
                        .filter(a -> a.getVehicleType().equalsIgnoreCase(vehicleType))
                        .toList();

        if (existingAppointments.size() >= 5) {
            return "Daily limit reached for " + vehicleType + " on " + date;
        }

        appointment.setStatus("pending");
        appointmentRepository.save(appointment);
        return "Appointment created successfully!";
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Get appointments by date
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }

    // Assign employee to an appointment (Admin use)
    public Appointment assignEmployee(String appointmentId, String employeeId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setAssignedEmployeeId(employeeId);
            appointment.setStatus("confirmed");
            return appointmentRepository.save(appointment);
        }
        return null;
    }
}
