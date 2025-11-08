package com.automobilesystem.automobile.Repository;
import com.automobilesystem.automobile.model.Appoinment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppoinmentRepo  extends MongoRepository<Appoinment, String> {
    List<Appoinment> findByCustomerId(String customerId);
    @Query("{'EmployeeId': ?0}")
    List<Appoinment> findByEmployeeId(String employeeId);
    
    // Admin dashboard methods
    List<Appoinment> findByAppointmentDate(java.time.LocalDate appointmentDate);
    List<Appoinment> findByAppointmentDateAndStatus(java.time.LocalDate appointmentDate, com.automobilesystem.automobile.model.AppointmentStatus status);
    List<Appoinment> findByStatus(com.automobilesystem.automobile.model.AppointmentStatus status);
    long countByStatus(com.automobilesystem.automobile.model.AppointmentStatus status);
    @Query(value = "{'EmployeeId': ?0}", count = true)
    long countByEmployeeId(String employeeId);
}
