package com.example.mmp.controller.rest;

import com.example.mmp.model.Doctor;
import com.example.mmp.repository.DoctorRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorRestController {

    private final DoctorRepository doctorRepo;

    public DoctorRestController(DoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    @GetMapping
    public List<Doctor> list(@RequestParam(required = false) String specialization) {
        if (specialization != null && !specialization.isBlank()) {
            return doctorRepo.findBySpecializationIgnoreCase(specialization);
        }
        return doctorRepo.findAll();
    }
}
