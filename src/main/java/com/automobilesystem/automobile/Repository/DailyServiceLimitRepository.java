package com.automobilesystem.automobile.repository;

import com.automobilesystem.automobile.model.DailyServiceLimit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyServiceLimitRepository extends MongoRepository<DailyServiceLimit, String> {
    Optional<DailyServiceLimit> findByDate(LocalDate date);
}
