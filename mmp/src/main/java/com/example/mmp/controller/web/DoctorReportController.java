package com.example.mmp.controller.web;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Report;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.ReportRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/doctor/reports")
public class DoctorReportController {

    private final AppointmentRepository apptRepo;
    private final ReportRepository reportRepo;

    @Value("${report.upload.dir:uploads}")
    private String uploadBaseDir;

    public DoctorReportController(AppointmentRepository apptRepo,
                                  ReportRepository reportRepo) {
        this.apptRepo = apptRepo;
        this.reportRepo = reportRepo;
    }

    private boolean isDoctorLogged(HttpSession session) {
        return session.getAttribute("doctorId") != null;
    }

    @PostMapping("/upload/{appointmentId}")
    public String uploadReport(@PathVariable Long appointmentId,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) throws IOException {

        if (!isDoctorLogged(session)) {
            return "redirect:/doctor/login";
        }

        Long doctorId = (Long) session.getAttribute("doctorId");

        Appointment appt = apptRepo.findById(appointmentId).orElse(null);

        // Appointment validation + ownership check
        if (appt == null || file.isEmpty()
                || !appt.getDoctor().getId().equals(doctorId)) {
            return "redirect:/doctor/home";
        }

        // Ensure upload directory exists
        Path uploadDir = Paths.get(uploadBaseDir);
        Files.createDirectories(uploadDir);

        // Create unique filename
        String storedFileName =
                System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path target = uploadDir.resolve(storedFileName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Save report metadata
        Report report = new Report();
        report.setAppointment(appt);
        report.setFileName(file.getOriginalFilename());
        report.setContentType(file.getContentType());
        report.setStoragePath(target.toString());

        reportRepo.save(report);

        return "redirect:/doctor/home";
    }
}
