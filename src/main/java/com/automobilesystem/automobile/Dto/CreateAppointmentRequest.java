package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.VehicleType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateAppointmentRequest(String clientId , String clientName , String employeeId , String employeeName
, String description , VehicleType vehicleType, LocalDate date, LocalDateTime timeSlot) {

}
