package com.automobilesystem.automobile.Service;


import com.automobilesystem.automobile.Dto.AppoinmentDto;
import com.automobilesystem.automobile.Dto.CreateAppointmentRequest;
import com.automobilesystem.automobile.Dto.StatusUpdateMessage;
import com.automobilesystem.automobile.Dto.UpdateStatusRequest;
import com.automobilesystem.automobile.Dto.AdminDashboardDtos.AppointmentDto;
import com.automobilesystem.automobile.Exceptions.UserIdNotFoundException;
import com.automobilesystem.automobile.Repository.AppointmentRepository;
import com.automobilesystem.automobile.Repository.CustomerRepo;
import com.automobilesystem.automobile.model.Appointment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerRepo customerRepo;
    
    @Autowired
    private AdminDashboardWebSocketService adminWebSocketService;



    public AppoinmentDto   createAppointment(CreateAppointmentRequest request) {
        var appointment = new Appointment();

        var customer = customerRepo.findById(request.clientId())
;

        if( customer == null){
            throw  new UserIdNotFoundException("id not   found with user id "+  request.clientId());

        }


        appointment.setCustomerId(request.clientId());
        appointment.setCustomerName(request.clientName());
        appointment.setEmployeeId(request.employeeId());
        appointment.setEmployeeName(request.employeeName());
        appointment.setDescription(request.description());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setStatusMessage("Appointment created, waiting for confirmation");

        Appointment saved = appointmentRepository.insert(appointment);

        // Notify via websocket that new appointment is made
        notifyStatusUpdate(saved,"System");
        
        // Notify admin dashboard about new appointment
        if (adminWebSocketService != null) {
            try {
                // Convert to admin DTO and send real-time notification
                var adminDto = convertToAdminDto(saved);
                adminWebSocketService.sendNewAppointment(adminDto);
            } catch (Exception e) {
                System.err.println("Error sending admin notification: " + e.getMessage());
            }
        }

        return convertToDTO(saved);


        // return the dto

    }

    public AppoinmentDto updateAppointmentStatus(String appointmentId,
                                                  UpdateStatusRequest request,
                                                  String employeeId) {
        var  appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate that the employee updating is assigned to this appointment
        if (!appointment.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized: You are not assigned to this appointment");
        }

        appointment.setStatus(request.status());
        appointment.setStatusMessage(request.statusMessage());
        appointment.setUpdatedAt(LocalDateTime.now());

        var  updated = appointmentRepository.save(appointment);

        // Send real-time update via WebSocket
        notifyStatusUpdate(updated, appointment.getEmployeeName());

        return convertToDTO(updated);
    }


    private void notifyStatusUpdate(Appointment appointment, String updateBy ) {
        StatusUpdateMessage message =    new StatusUpdateMessage(
                appointment.getId(),
                appointment.getStatus(),
                appointment.getStatusMessage(),
                appointment.getUpdatedAt(),
                updateBy
        );
        // Send to specific appointment topic
        // Clients subscribe to /topic/appointment/{appointmentId}
        messagingTemplate.convertAndSend(
                "/topic/appointment/" + appointment.getId(),
                message
        );

        // Also send to client-specific topic
        // Clients can subscribe to /topic/client/{clientId} to see all their appointments
        messagingTemplate.convertAndSend(
                "/topic/client/" + appointment.getCustomerId(),
                message
        );
    }

    public AppoinmentDto getAppointmentById(String appointmentId) {
        var  appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return convertToDTO(appointment);
    }
    public List<AppoinmentDto> getClientAppointments(String clientId) {
        return appointmentRepository.findByCustomerId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<AppoinmentDto> getEmployeeAppointments(String employeeId) {
        return appointmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppoinmentDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AppoinmentDto convertToDTO(Appointment appointment) {
        return new AppoinmentDto(
                appointment.getId(),
                appointment.getCustomerId(),
                appointment.getCustomerName(),
                appointment.getEmployeeId(),
                appointment.getEmployeeName(),
                appointment.getStatus(),
                appointment.getDescription(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getStatusMessage()
        );
    }

    private AppointmentDto convertToAdminDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getCustomerId(),
                appointment.getCustomerName(),
                null, // vehicleType - not available in current model
                null, // appointmentDate - not available in current model  
                null, // timeSlot - not available in current model
                appointment.getStatus(),
                appointment.getEmployeeId(),
                appointment.getEmployeeName(),
                appointment.getCreatedAt() != null ? appointment.getCreatedAt().toString() : null,
                appointment.getDescription()
        );
    }
}
