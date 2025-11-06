package com.automobilesystem.automobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    private String appointmentId;

    private String customerId; // reference to Customer
    private String vehicleType; // e.g. "Car", "Van", "Truck", "Motorcycle"
    private LocalDate date; // appointment date
    private String status; // pending, confirmed, completed
    private String assignedEmployeeId; // optional - assigned later by admin
}
