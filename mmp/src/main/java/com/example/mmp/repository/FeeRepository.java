package com.example.mmp.repository;

import com.example.mmp.model.Fee;
import com.example.mmp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByAppointment_Patient(Patient patient);
    List<Fee> findByAppointmentPatientId(Long patientId);
}
