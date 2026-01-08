package com.example.mmp.repository;

import com.example.mmp.model.Report;
import com.example.mmp.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Fetch all reports for a specific appointment
     * (Used by Admin / Doctor)
     */
    List<Report> findByAppointment(Appointment appointment);

    /**
     * Fetch all reports for a specific appointment ID
     * (Cleaner when you only have appointmentId)
     */
    List<Report> findByAppointmentId(Long appointmentId);

    /**
     * Fetch all reports for a patient (via appointment)
     * (Used by Patient module)
     */
    List<Report> findByAppointmentPatientId(Long patientId);
    
  
}
