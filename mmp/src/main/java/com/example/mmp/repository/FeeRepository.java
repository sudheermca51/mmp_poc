package com.example.mmp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mmp.model.Fee;
import com.example.mmp.model.Patient;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    // Find fee by appointment (used during report upload to prevent duplicate fee)
    Optional<Fee> findByAppointmentId(Long appointmentId);

    // Patient fee history
    List<Fee> findByAppointment_Patient(Patient patient);

    // Patient fee history using patientId
    List<Fee> findByAppointmentPatientId(Long patientId);
}