package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

import java.time.LocalDateTime;

public record StatusUpdateMessage(String appoinetmentId, AppointmentStatus status , String statusMessage ,
                                  LocalDateTime   updateAt ,String updatedBy ) {
}
