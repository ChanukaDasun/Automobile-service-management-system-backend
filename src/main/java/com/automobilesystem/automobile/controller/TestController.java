package com.automobilesystem.automobile.controller;
import com.automobilesystem.automobile.repository.CustomerRepo;
import com.automobilesystem.automobile.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private  final CustomerRepo customerRepo;

    @GetMapping
    public String test(){
        return "test";
    }




    @GetMapping("/api")
    public String test2 (){

        var customer = new Customer("dasd","randiar","dadsad","233131313");
        customerRepo.save(customer);

        return "success";
    }
}
