package com.automobilesystem.automobile.repository;

import com.automobilesystem.automobile.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Find appointments by date
    List<Appointment> findByDate(LocalDate date);

    // Find appointments by vehicle type
//    List<Appointment> findByVehicleType(String vehicleType);
}
