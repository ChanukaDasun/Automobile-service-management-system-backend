package com.automobilesystem.automobile.model;

public enum AppointmentStatus
{
    PENDING,
    CONFIRMED,      // Employee confirmed
    ASSIGNED,       // Added for employee dashboard - task assigned to employee
    IN_PROGRESS,    // Work started
    COMPLETED,      // Work finished
    CANCELLED       // Appointment cancelled (Note: Employees cannot see cancelled appointments)
}
