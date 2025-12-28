package com.example.mmp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mmp.model.Patient;

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

    List<Patient> findByApprovedTrue(Sort sort);

    List<Patient> findByRejectedTrue(Sort sort);
    
    Page<Patient> findByApprovedTrue(Pageable pageable);

    Page<Patient> findByRejectedTrue(Pageable pageable);

}
