package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppoinmentDto(String id , String clientId , String clientName , String employeeId , String employeeName
,
                             AppointmentStatus appointmentStatus,String  description ,
                            LocalDateTime createdAt ,LocalDateTime updateAt ,String statusMessage
                            ) {
}
