package com.automobilesystem.automobile.controller;


import com.automobilesystem.automobile.Dto.AppoinmentDto;
import com.automobilesystem.automobile.Dto.CreateAppointmentRequest;
import com.automobilesystem.automobile.Dto.UpdateStatusRequest;
import com.automobilesystem.automobile.Service.AppoinmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin( origins = "*")
public class AppoinmentController {
    private  final AppoinmentService appoinmentService;


    @PostMapping
    @ResponseStatus( HttpStatus.CREATED)
    public AppoinmentDto createAppointmentRequest(@RequestBody CreateAppointmentRequest  request) {

        var created = appoinmentService.createAppointment(request);

        return created;



    }

    @PutMapping("/{id}/status")
    @ResponseStatus( HttpStatus.OK)


    public AppoinmentDto updateStatus (@PathVariable String id ,
                                       @RequestBody UpdateStatusRequest request ,@RequestHeader("Employee-id") String employeeID){

        var update = appoinmentService.updateAppointmentStatus(  id , request, employeeID);


        return update;


    }
    @GetMapping("/{id}")
    @ResponseStatus( HttpStatus.OK)
    public AppoinmentDto getAppointment(@PathVariable String id) {
        return  appoinmentService.getAppointmentById(id);

    }
    @GetMapping("/client/{clientId}")
    @ResponseStatus( HttpStatus.OK)
    public List<AppoinmentDto> getClientAppointments(
            @PathVariable String clientId) {
        var appointments = appoinmentService.getClientAppointments(clientId);
        return appointments;
    }

    @GetMapping("/employee/{employeeId}")
    public List<AppoinmentDto> getEmployeeAppointments(
            @PathVariable String employeeId) {
          return  appoinmentService.getEmployeeAppointments(employeeId);

    }

    @GetMapping("/debug/all")
    @ResponseStatus(HttpStatus.OK)
    public List<AppoinmentDto> getAllAppointmentsDebug() {
        return appoinmentService.getAllAppointments();
    }

}
