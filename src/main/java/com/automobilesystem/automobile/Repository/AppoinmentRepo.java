package com.automobilesystem.automobile.Repository;


import com.automobilesystem.automobile.model.Appoinment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppoinmentRepo  extends MongoRepository<Appoinment, String> {
    List<Appoinment> findByCustomerId( String customerId );

    @Query("{'EmployeeId': ?0}")
    List<Appoinment> findByEmployeeId(String employeeId);

    // Employee dashboard methods - added safely without modifying existing methods
    @Query("{'EmployeeId': ?0, 'status': ?1}")
    List<Appoinment> findByEmployeeIdAndStatus(String employeeId, AppointmentStatus status);
    
    @Query("{'EmployeeId': ?0, 'appointmentDate': ?1}")
    List<Appoinment> findByEmployeeIdAndAppointmentDate(String employeeId, LocalDate appointmentDate);
    
    @Query("{'EmployeeId': ?0, 'appointmentDate': {$gte: ?1, $lte: ?2}}")
    List<Appoinment> findByEmployeeIdAndAppointmentDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);
    
    @Query(value = "{'EmployeeId': ?0, 'status': ?1}", count = true)
    long countByEmployeeIdAndStatus(String employeeId, AppointmentStatus status);
    
    @Query("{'EmployeeId': ?0}")
    List<Appoinment> findTodayTasksByEmployee(String employeeId, LocalDate today);
    
    @Query("{'EmployeeId': ?0, 'appointmentDate': {$lt: ?1}}")
    List<Appoinment> findPastTasksByEmployee(String employeeId, LocalDate today);




}
