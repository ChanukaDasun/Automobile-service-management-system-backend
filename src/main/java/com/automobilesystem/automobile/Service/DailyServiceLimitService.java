package com.automobilesystem.automobile.service;

import com.automobilesystem.automobile.model.DailyServiceLimit;
import com.automobilesystem.automobile.repository.DailyServiceLimitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DailyServiceLimitService {

    private final DailyServiceLimitRepository repository;

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
}
