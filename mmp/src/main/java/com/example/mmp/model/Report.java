package com.example.mmp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Each report belongs to exactly one appointment.
     * Patient is linked indirectly via Appointment.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    /**
     * Original file name uploaded (for display)
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * MIME type (application/pdf, image/png, etc.)
     */
    @Column(nullable = false)
    private String contentType;

    /**
     * Physical storage path (file system or cloud)
     */
    @Column(nullable = false)
    private String storagePath;

    /**
     * Upload timestamp
     */
    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    /* =======================
       Getters and Setters
       ======================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
