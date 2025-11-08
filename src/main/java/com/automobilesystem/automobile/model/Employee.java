package com.automobilesystem.automobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    private String employeeId;
    private String name;
    private String email;
    private boolean availability = true; // true = available, false = busy
    private int assignedAppointments = 0; // Current number of assigned appointments
}
