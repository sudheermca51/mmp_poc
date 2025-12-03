package com.example.mmp.repository;

import com.example.mmp.model.Report;
import com.example.mmp.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByAppointment(Appointment appointment);
}
