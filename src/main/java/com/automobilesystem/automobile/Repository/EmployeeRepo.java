package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, String> {

    // Existing methods - keep for compatibility
    List<Employee> findByAvailability(boolean availability);
    Optional<Employee> findByEmployeeId(String employeeId);

    // Find employees by email
    Optional<Employee> findByEmail(String email);

    // Find available employees (availability = true)
    @Query("{'availability': true}")
    List<Employee> findAvailableEmployees();

    // Count available employees
    @Query(value = "{'availability': true}", count = true)
    long countAvailableEmployees();

    // Count employees by availability status
    long countByAvailability(boolean availability);
}