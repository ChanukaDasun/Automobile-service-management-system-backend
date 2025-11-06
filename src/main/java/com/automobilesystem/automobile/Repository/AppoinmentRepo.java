package com.automobilesystem.automobile.Repository;
import com.automobilesystem.automobile.model.Appoinment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppoinmentRepo  extends MongoRepository<Appoinment, String> {
    List<Appoinment> findByCustomerId( String customerId );

    List<Appoinment> findByEmployeeId(String employeeId);
    
    // ADDED: Find appointments by date and status list
    // This is used to get all PENDING and CONFIRMED appointments for a specific date
    // to check if daily limit has been reached
    List<Appoinment> findByAppointmentDateAndStatusIn(LocalDate date, List<AppointmentStatus> statuses);
    
    // ADDED: Count appointments by date and status list
    // This efficiently counts how many PENDING and CONFIRMED appointments exist for a date
    // without loading all the appointment data into memory
    long countByAppointmentDateAndStatusIn(LocalDate date, List<AppointmentStatus> statuses);
}
