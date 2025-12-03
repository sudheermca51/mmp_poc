package com.example.mmp.repository;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Patient;
import com.example.mmp.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByAppointmentDateTimeDesc(Patient patient);
    List<Appointment> findByDoctorAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            Doctor doctor, LocalDateTime from, LocalDateTime to);
}
