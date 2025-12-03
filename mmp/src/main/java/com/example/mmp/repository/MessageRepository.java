package com.example.mmp.repository;

import com.example.mmp.model.Message;
import com.example.mmp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPatientOrderByCreatedAtDesc(Patient patient);
    List<Message> findAllByOrderByCreatedAtDesc();
}
