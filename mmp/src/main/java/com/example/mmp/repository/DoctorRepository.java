package com.example.mmp.repository;

import com.example.mmp.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Existing logic (KEEP)
    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    // 🔐 NEW: for Doctor login
    Optional<Doctor> findByUsername(String username);
}