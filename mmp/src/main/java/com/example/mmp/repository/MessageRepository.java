package com.example.mmp.repository;

import com.example.mmp.model.Message;
import com.example.mmp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPatientOrderByCreatedAtDesc(Patient patient);
    List<Message> findAllByOrderByCreatedAtDesc();
    List<Message> findByPatient(Patient patient);
    List<Message> findByPatientAndStatus(Patient patient, String status);
    
    
    /* ✅ Inbox: only patients who have messages */
    @Query("select distinct m.patient from Message m")
    List<Patient> findDistinctPatients();

    /* ✅ Latest message per patient */
    Message findTopByPatientOrderByCreatedAtDesc(Patient patient);
 


}
