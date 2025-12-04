package com.example.mmp.repository;

import com.example.mmp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
    Optional<Patient> findByUsernameIgnoreCase(String username);

    Optional<Patient> findByUsernameAndApprovedTrue(String username);

    boolean existsByUsername(String username);

    /** Convenience that excludes rejected users (if you need it) */
    Optional<Patient> findByUsernameAndApprovedTrueAndRejectedFalse(String username);
    

    // Derived query — Spring Data will create this automatically
    Optional<Patient> findByUsernameIgnoreCaseAndApprovedTrueAndRejectedFalse(String username);

  
}
