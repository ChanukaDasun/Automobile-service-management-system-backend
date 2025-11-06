// UPDATED: Fixed package name to match folder structure (Repository with capital R)
package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.DailyServiceLimit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyServiceLimitRepository extends MongoRepository<DailyServiceLimit, String> {
    // EXISTING: Find daily limit configuration by date
    Optional<DailyServiceLimit> findByDate(LocalDate date);
}
