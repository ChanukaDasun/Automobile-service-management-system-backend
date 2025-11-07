package com.automobilesystem.automobile.Service;


import com.automobilesystem.automobile.Dto.AppoinmentDto;
import com.automobilesystem.automobile.Dto.CreateAppointmentRequest;
import com.automobilesystem.automobile.Dto.StatusUpdateMessage;
import com.automobilesystem.automobile.Dto.UpdateStatusRequest;
import com.automobilesystem.automobile.Exceptions.UserIdNotFoundException;
import com.automobilesystem.automobile.Repository.AppoinmentRepo;
import com.automobilesystem.automobile.Repository.CustomerRepo;
import com.automobilesystem.automobile.model.Appoinment;
import com.automobilesystem.automobile.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AppoinmentService {
    private final AppoinmentRepo appoinmentRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerRepo customerRepo;



    public AppoinmentDto   createAppointment(CreateAppointmentRequest request) {
        var appointment = new Appoinment();

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

        Appoinment saved = appoinmentRepo.insert(appointment);


        // notifiy via websocket that new appoinemtn is made

        notifyStatusUpdate(saved,"System");

        return convertToDTO(saved);


        // return the dto

    }

    public AppoinmentDto updateAppointmentStatus(String appointmentId,
                                                  UpdateStatusRequest request,
                                                  String employeeId) {
        var  appointment = appoinmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate that the employee updating is assigned to this appointment
        if (!appointment.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized: You are not assigned to this appointment");
        }

        appointment.setStatus(request.status());
        appointment.setStatusMessage(request.statusMessage());
        appointment.setUpdatedAt(LocalDateTime.now());

        var  updated = appoinmentRepo.save(appointment);

        // Send real-time update via WebSocket
        notifyStatusUpdate(updated, appointment.getEmployeeName());

        return convertToDTO(updated);
    }


    private void notifyStatusUpdate(Appoinment appoinment, String updateBy ) {
        StatusUpdateMessage message =    new StatusUpdateMessage(
                appoinment.getAppoinmentId(),
                appoinment.getStatus(),
                appoinment.getStatusMessage(),
                appoinment.getUpdatedAt(),
                updateBy
        );
        // Send to specific appointment topic
        // Clients subscribe to /topic/appointment/{appointmentId}
        messagingTemplate.convertAndSend(
                "/topic/appointment/" + appoinment.getAppoinmentId(),
                message
        );

        // Also send to client-specific topic
        // Clients can subscribe to /topic/client/{clientId} to see all their appointments
        messagingTemplate.convertAndSend(
                "/topic/client/" + appoinment.getCustomerId(),
                message
        );
    }

    public AppoinmentDto getAppointmentById(String appointmentId) {
        var  appointment = appoinmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return convertToDTO(appointment);
    }
    public List<AppoinmentDto> getClientAppointments(String clientId) {
        return appoinmentRepo.findByCustomerId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<AppoinmentDto> getEmployeeAppointments(String employeeId) {
        return appoinmentRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppoinmentDto> getAllAppointments() {
        return appoinmentRepo.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AppoinmentDto convertToDTO(Appoinment appointment) {
        return new AppoinmentDto(
                appointment.getAppoinmentId(),
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



}
