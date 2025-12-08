package com.example.mmp.controller.rest;

import com.example.mmp.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserAvailabilityRestController {

    private final PatientRepository patientRepository;

    public UserAvailabilityRestController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {

        String clean = username.trim().toLowerCase();

        boolean valid = clean.matches("^[a-z0-9._-]{3,30}$");
        boolean available = false;

        if (valid) {
            available = !patientRepository.existsByUsername(clean);
        }

        return Map.of(
                "username", clean,
                "validFormat", valid,
                "available", available
        );
    }
}
