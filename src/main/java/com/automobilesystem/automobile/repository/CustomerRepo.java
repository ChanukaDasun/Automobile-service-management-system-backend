package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends MongoRepository<Customer,String> {
}
