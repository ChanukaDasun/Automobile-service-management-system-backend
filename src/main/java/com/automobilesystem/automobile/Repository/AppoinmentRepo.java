package com.automobilesystem.automobile.repository;
import com.automobilesystem.automobile.model.Appoinment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppoinmentRepo  extends MongoRepository<Appoinment, String> {
    List<Appoinment> findByCustomerId( String customerId );


    List<Appoinment> findByEmployeeId(String employeeId);




}
