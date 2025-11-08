package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Dto.AdminDashboardDtos.AppointmentDto;
import com.automobilesystem.automobile.Dto.AdminDashboardDtos.AppointmentStatsDto;
import com.automobilesystem.automobile.Dto.AdminDashboardDtos.EmployeeDto;
import com.automobilesystem.automobile.Dto.ClerkUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send real-time appointment update to all admin clients
     */
    public void sendAppointmentUpdate(AppointmentDto appointment) {
        messagingTemplate.convertAndSend("/topic/admin/appointments", Map.of(
            "type", "APPOINTMENT_UPDATE",
            "data", appointment,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time appointment creation to all admin clients
     */
    public void sendNewAppointment(AppointmentDto appointment) {
        messagingTemplate.convertAndSend("/topic/admin/appointments", Map.of(
            "type", "NEW_APPOINTMENT",
            "data", appointment,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time appointment status change to all admin clients
     */
    public void sendAppointmentStatusChange(AppointmentDto appointment) {
        messagingTemplate.convertAndSend("/topic/admin/appointments", Map.of(
            "type", "STATUS_CHANGE",
            "data", appointment,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time appointment assignment to all admin clients
     */
    public void sendAppointmentAssignment(AppointmentDto appointment) {
        messagingTemplate.convertAndSend("/topic/admin/appointments", Map.of(
            "type", "APPOINTMENT_ASSIGNED",
            "data", appointment,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time statistics update to all admin clients
     */
    public void sendStatsUpdate(AppointmentStatsDto stats) {
        messagingTemplate.convertAndSend("/topic/admin/stats", Map.of(
            "type", "STATS_UPDATE",
            "data", stats,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time employee availability update to all admin clients
     */
    public void sendEmployeeUpdate(List<EmployeeDto> employees) {
        messagingTemplate.convertAndSend("/topic/admin/employees", Map.of(
            "type", "EMPLOYEES_UPDATE",
            "data", employees,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time Clerk users update to all admin clients
     */
    public void sendClerkUsersUpdate(List<ClerkUserDto> users) {
        messagingTemplate.convertAndSend("/topic/admin/clerk/users", Map.of(
            "type", "CLERK_USERS_UPDATE",
            "data", users,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send real-time Clerk stats update to all admin clients
     */
    public void sendClerkStatsUpdate(Map<String, Object> stats) {
        messagingTemplate.convertAndSend("/topic/admin/clerk/stats", Map.of(
            "type", "CLERK_STATS_UPDATE",
            "data", stats,
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send general admin dashboard refresh signal
     */
    public void sendDashboardRefresh() {
        messagingTemplate.convertAndSend("/topic/admin/refresh", Map.of(
            "type", "DASHBOARD_REFRESH",
            "message", "Dashboard data has been updated",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Send notification to specific admin user
     */
    public void sendAdminNotification(String adminUserId, String message, String type) {
        messagingTemplate.convertAndSendToUser(adminUserId, "/queue/admin/notifications", Map.of(
            "type", type,
            "message", message,
            "timestamp", System.currentTimeMillis()
        ));
    }
}