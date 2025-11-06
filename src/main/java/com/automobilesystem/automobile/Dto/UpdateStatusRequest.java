package com.automobilesystem.automobile.Dto;

import com.automobilesystem.automobile.model.AppointmentStatus;

public record UpdateStatusRequest(AppointmentStatus status ,String statusMessage) {
}
