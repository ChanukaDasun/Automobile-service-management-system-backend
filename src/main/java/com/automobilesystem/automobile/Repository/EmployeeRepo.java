package com.automobilesystem.automobile.repository;


import com.automobilesystem.automobile.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, String>
{
}
