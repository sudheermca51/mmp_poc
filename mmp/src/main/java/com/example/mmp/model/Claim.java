package com.example.mmp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* -----------------------------
       RELATIONSHIPS
       ----------------------------- */

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private Appointment appointment;

    @ManyToOne(optional = false)
    private Fee fee;

    /* -----------------------------
       CLAIM DETAILS
       ----------------------------- */

    private BigDecimal claimAmount;

    private String status; // SUBMITTED / APPROVED / REJECTED

    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime processedAt;

    /* -----------------------------
       GETTERS & SETTERS
       ----------------------------- */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
