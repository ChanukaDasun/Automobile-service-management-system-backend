package com.automobilesystem.automobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "daily_service_limits")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DailyServiceLimit {
    @Id
    private String id;
    
    private LocalDate date;
    
    private Integer maxVehicles;
    
    private String notes;
}
