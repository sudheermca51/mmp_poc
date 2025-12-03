package com.example.mmp.controller.rest;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import com.example.mmp.repository.PatientRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentRestController {

    private final AppointmentRepository apptRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    public AppointmentRestController(AppointmentRepository apptRepo,
                                     PatientRepository patientRepo,
                                     DoctorRepository doctorRepo) {
        this.apptRepo = apptRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> byPatient(@PathVariable Long patientId) {
        Patient p = patientRepo.findById(patientId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        List<Appointment> list = apptRepo.findByPatientOrderByAppointmentDateTimeDesc(p);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestParam Long patientId,
                                              @RequestParam Long doctorId,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                                              @RequestParam String reason) {
        Patient p = patientRepo.findById(patientId).orElse(null);
        Doctor d = doctorRepo.findById(doctorId).orElse(null);
        if (p == null || d == null) return ResponseEntity.badRequest().build();
        Appointment a = new Appointment();
        a.setPatient(p);
        a.setDoctor(d);
        a.setAppointmentDateTime(dateTime);
        a.setReason(reason);
        a.setStatus("SCHEDULED");
        apptRepo.save(a);
        return ResponseEntity.ok(a);
    }
}
