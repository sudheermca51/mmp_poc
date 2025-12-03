package com.example.mmp.controller.web;

import com.example.mmp.model.Fee;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.FeeRepository;
import com.example.mmp.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/patient/fees")
public class PatientFeesController {

    private final PatientRepository patientRepo;
    private final FeeRepository feeRepo;

    public PatientFeesController(PatientRepository patientRepo, FeeRepository feeRepo) {
        this.patientRepo = patientRepo;
        this.feeRepo = feeRepo;
    }

    private Patient getLoggedPatient(HttpSession session) {
        Object idObj = session.getAttribute("patientId");
        if (idObj == null) return null;
        Long id = (Long) idObj;
        return patientRepo.findById(id).orElse(null);
    }

    @GetMapping
    public String fees(Model model, HttpSession session) {
        Patient p = getLoggedPatient(session);
        if (p == null) return "redirect:/patient/login";
        List<Fee> fees = feeRepo.findByAppointment_Patient(p);
        model.addAttribute("patient", p);
        model.addAttribute("fees", fees);
        return "patient/patient-fees";
    }
}
