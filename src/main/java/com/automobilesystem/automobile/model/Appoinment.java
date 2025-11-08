package com.automobilesystem.automobile.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String customerId ;
    private String EmployeeId ;
    private String CustomerName ;
    private String  EmployeeName ;
    private AppointmentStatus status;
    private String description ;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt ;
    private String statusMessage;
    
    // Admin dashboard fields
    private String vehicleType;
    private String timeSlot;
    private java.time.LocalDate appointmentDate;



}
