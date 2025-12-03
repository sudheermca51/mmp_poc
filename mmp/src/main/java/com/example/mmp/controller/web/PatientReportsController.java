package com.example.mmp.controller.web;

import com.example.mmp.model.Patient;
import com.example.mmp.model.Report;
import com.example.mmp.repository.PatientRepository;
import com.example.mmp.repository.ReportRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient/reports")
public class PatientReportsController {

    private final PatientRepository patientRepo;
    private final ReportRepository reportRepo;

    public PatientReportsController(PatientRepository patientRepo, ReportRepository reportRepo) {
        this.patientRepo = patientRepo;
        this.reportRepo = reportRepo;
    }

    private Patient getLoggedPatient(HttpSession session) {
        Object idObj = session.getAttribute("patientId");
        if (idObj == null) return null;
        Long id = (Long) idObj;
        return patientRepo.findById(id).orElse(null);
    }

    @GetMapping
    public String reports(Model model, HttpSession session) {
        Patient p = getLoggedPatient(session);
        if (p == null) return "redirect:/patient/login";

        List<Report> reports = reportRepo.findAll().stream()
                .filter(r -> r.getAppointment().getPatient().getId().equals(p.getId()))
                .collect(Collectors.toList());

        model.addAttribute("patient", p);
        model.addAttribute("reports", reports);
        return "patient/patient-reports";
    }
}
