package com.example.mmp.controller.web;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.mmp.model.Patient;
import com.example.mmp.model.Report;
import com.example.mmp.repository.PatientRepository;
import com.example.mmp.repository.ReportRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient/reports")
public class PatientReportsController {

    private final PatientRepository patientRepo;
    private final ReportRepository reportRepo;

    public PatientReportsController(PatientRepository patientRepo,
                                    ReportRepository reportRepo) {
        this.patientRepo = patientRepo;
        this.reportRepo = reportRepo;
    }

    /**
     * Resolve logged-in patient using patientId stored in session.
     * This is the single source of truth for patient authentication here.
     */
    private Patient getLoggedPatient(HttpSession session) {
        Object idObj = session.getAttribute("patientId");
        if (idObj == null) {
            return null;
        }
        Long patientId = (Long) idObj;
        return patientRepo.findById(patientId).orElse(null);
    }

    /**
     * Patient reports page
     */
    @GetMapping
    public String reports(Model model, HttpSession session) {

        Patient patient = getLoggedPatient(session);
        if (patient == null) {
            // Session missing or expired → redirect to login
            return "redirect:/patient/login";
        }

        // Fetch reports securely via DB (appointment → patient)
        List<Report> reports =
                reportRepo.findByAppointmentPatientId(patient.getId());

        model.addAttribute("patient", patient);
        model.addAttribute("reports", reports);
        model.addAttribute("activeMenu","reports");
        return "patient/patient-reports";
    }

    /**
     * Secure report download (NO direct file exposure)
     */
    @GetMapping("/download/{reportId}")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable Long reportId,
            HttpSession session) throws IOException {

        Object idObj = session.getAttribute("patientId");
        if (idObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long patientId = (Long) idObj;

        Report report = reportRepo.findById(reportId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        // 🔒 Ownership check: patient can access ONLY their own reports
        if (!report.getAppointment().getPatient().getId().equals(patientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Physical file path = uploads + relative storagePath from DB
        Path filePath = Paths.get("uploads").resolve(report.getStoragePath());

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\""
                )
                .body(resource);
    }
    @GetMapping("/view/{reportId}")
    public ResponseEntity<Resource> viewReport(
            @PathVariable Long reportId,
            HttpSession session) throws IOException {

        Long patientId = (Long) session.getAttribute("patientId");
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Report report = reportRepo.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 🔒 ownership check
        if (!report.getAppointment().getPatient().getId().equals(patientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Path filePath = Paths.get(report.getStoragePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(report.getContentType()))
                // 🔑 INLINE is the key difference
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + report.getFileName() + "\"")
                .body(resource);
    }

}