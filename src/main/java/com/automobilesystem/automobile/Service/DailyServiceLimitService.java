// UPDATED: Fixed package name to match folder structure (Service with capital S)
package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.model.DailyServiceLimit;
// UPDATED: Fixed import to use Repository with capital R
import com.automobilesystem.automobile.Repository.DailyServiceLimitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DailyServiceLimitService {

    private final DailyServiceLimitRepository repository;
    // ADDED: Default daily limit constant - used when no specific limit is configured for a date
    private static final int DEFAULT_DAILY_LIMIT = 5;

    public DailyServiceLimitService(DailyServiceLimitRepository repository) {
        this.repository = repository;
    }

    public DailyServiceLimit create(DailyServiceLimit limit) {
        return repository.save(limit);
    }

    public List<DailyServiceLimit> getAll() {
        return repository.findAll();
    }

    public Optional<DailyServiceLimit> getById(String id) {
        return repository.findById(id);
    }

    public Optional<DailyServiceLimit> getByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public DailyServiceLimit update(String id, DailyServiceLimit limit) {
        limit.setId(id);
        return repository.save(limit);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    // ADDED: New method to get limit for a specific date
    // Returns configured limit from database, or defaults to 5 if not found
    /**
     * Get the daily appointment limit for a specific date
     * @param date The date to check
     * @return The configured limit, or 5 if no limit is configured for that date
     */
    public int getLimitForDate(LocalDate date) {
        return repository.findByDate(date)
                .map(DailyServiceLimit::getMaxVehicles)
                .orElse(DEFAULT_DAILY_LIMIT);
    }
}
