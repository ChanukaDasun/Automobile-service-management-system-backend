package com.automobilesystem.automobile.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document( collection = "appoiment")
public class Appoinment {


    @Id
    private String AppoinmentId;
    private String CustomerId ;
    private String EmployeeId ;
    private String CustomerName ;
    private String  EmployeeName ;
    // adding the enum Appoinement status
    private AppointmentStatus status;
    private String description ;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt ;
    private String statusMessage;
    
    // ADDED: New field to store the scheduled appointment date
    // This is used to check daily limits and query appointments by date
    // Format: YYYY-MM-DD (e.g., 2025-11-10)
    private LocalDate appointmentDate;



}
