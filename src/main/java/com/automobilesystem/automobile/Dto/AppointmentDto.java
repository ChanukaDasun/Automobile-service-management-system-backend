package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;
import com.automobilesystem.automobile.model.VehicleType;

import java.time.LocalDateTime;

public record AppointmentDto(String id , String clientId , String clientName , String employeeId , String employeeName,
VehicleType vehicleType , LocalDateTime timeSlot

,
                             AppointmentStatus appointmentStatus, String  description ,
                             LocalDateTime createdAt , LocalDateTime updateAt , String statusMessage
                            ) {
}
