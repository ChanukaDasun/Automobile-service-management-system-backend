package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.Appoinment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppoinmentRepo extends MongoRepository<Appoinment, String> {

    // Use lowercase field names to match what's actually in MongoDB
    List<Appoinment> findByCustomerId(String customerId);

    // Use lowercase 'employeeId' field name
    @Query("{'employeeId': ?0}")
    List<Appoinment> findByEmployeeId(String employeeId);

    @Query(value = "{'employeeId': ?0}", count = true)
    long countByEmployeeId(String employeeId);

    // Admin dashboard methods
    List<Appoinment> findByAppointmentDate(LocalDate appointmentDate);
    List<Appoinment> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);
    List<Appoinment> findByStatus(AppointmentStatus status);

    // Status count methods
    long countByStatus(AppointmentStatus status);

    // Find unassigned appointments
    @Query("{'employeeId': {$in: [null, '']}}")
    List<Appoinment> findUnassignedAppointments();

    // Count unassigned appointments
    @Query(value = "{'employeeId': {$in: [null, '']}}", count = true)
    long countUnassignedAppointments();
}