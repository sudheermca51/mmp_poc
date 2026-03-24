package com.example.mmp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Patient;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByAppointmentDateTimeDesc(Patient patient);
    List<Appointment> findByDoctorAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(
            Doctor doctor, LocalDateTime from, LocalDateTime to);

    // fetch appointments for a patient
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
    
    @Query("""
    	       SELECT a FROM Appointment a
    	       LEFT JOIN Report r ON r.appointment = a
    	       WHERE a.patient.id = :patientId
    	       AND a.status = 'COMPLETED'
    	       AND r.id IS NULL
    	       """)
    	List<Appointment> findCompletedAppointmentsWithoutReport(Long patientId);

}
