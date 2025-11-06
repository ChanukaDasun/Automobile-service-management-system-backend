package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.model.DailyServiceLimit;
// UPDATED: Fixed import to use Service with capital S (matches the package structure)
import com.automobilesystem.automobile.Service.DailyServiceLimitService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DailyServiceLimitService dailyServiceLimitService;

    public AdminController(DailyServiceLimitService dailyServiceLimitService) {
        this.dailyServiceLimitService = dailyServiceLimitService;
    }

    /**
     * Create a new daily service limit
     * POST /api/admin/daily-limits
     * Body: { "date": "2025-11-06", "maxVehicles": 50, "notes": "..." }
     */
    @PostMapping("/daily-limits")
    public ResponseEntity<DailyServiceLimit> createDailyLimit(@RequestBody DailyServiceLimit limit) {
        DailyServiceLimit created = dailyServiceLimitService.create(limit);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all daily service limits
     * GET /api/admin/daily-limits
     */
    @GetMapping("/daily-limits")
    public ResponseEntity<List<DailyServiceLimit>> getAllDailyLimits() {
        List<DailyServiceLimit> limits = dailyServiceLimitService.getAll();
        return ResponseEntity.ok(limits);
    }

    /**
     * Get daily service limit by ID
     * GET /api/admin/daily-limits/{id}
     */
    @GetMapping("/daily-limits/{id}")
    public ResponseEntity<DailyServiceLimit> getDailyLimitById(@PathVariable String id) {
        Optional<DailyServiceLimit> limit = dailyServiceLimitService.getById(id);
        return limit.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get daily service limit by date
     * GET /api/admin/daily-limits/by-date?date=2025-11-06
     */
    @GetMapping("/daily-limits/by-date")
    public ResponseEntity<DailyServiceLimit> getDailyLimitByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<DailyServiceLimit> limit = dailyServiceLimitService.getByDate(date);
        return limit.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update daily service limit
     * PUT /api/admin/daily-limits/{id}
     * Body: { "date": "2025-11-06", "maxVehicles": 60, "notes": "..." }
     */
    @PutMapping("/daily-limits/{id}")
    public ResponseEntity<DailyServiceLimit> updateDailyLimit(
            @PathVariable String id,
            @RequestBody DailyServiceLimit limit) {
        DailyServiceLimit updated = dailyServiceLimitService.update(id, limit);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete daily service limit
     * DELETE /api/admin/daily-limits/{id}
     */
    @DeleteMapping("/daily-limits/{id}")
    public ResponseEntity<Void> deleteDailyLimit(@PathVariable String id) {
        dailyServiceLimitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
