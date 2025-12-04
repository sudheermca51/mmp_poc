package com.example.mmp.controller.web;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import com.example.mmp.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientDashboardController {

    private final PatientRepository patientRepo;
    private final AppointmentRepository apptRepo;
    private final DoctorRepository doctorRepo;

    public PatientDashboardController(PatientRepository patientRepo,
                                      AppointmentRepository apptRepo,
                                      DoctorRepository doctorRepo) {
        this.patientRepo = patientRepo;
        this.apptRepo = apptRepo;
        this.doctorRepo = doctorRepo;
    }

//    private Patient getLoggedPatient(HttpSession session) {
//        Object idObj = session.getAttribute("patientId");
//        if (idObj == null) return null;
//        Long id = (Long) idObj;
//        return patientRepo.findById(id).orElse(null);
//    }
    private Patient getLoggedPatient(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (obj == null) return null;

        // If you already stored the Patient object
        if (obj instanceof Patient) {
            return (Patient) obj;
        }

        // If you stored username string (recommended)
        if (obj instanceof String) {
            String username = ((String) obj).trim();
            return patientRepo.findByUsername(username).orElse(null);
        }

        return null;
    }
    @GetMapping("/home")
    public String home(@RequestParam(required = false) String specializationFilter,
                       Model model,
                       HttpSession session) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) return "redirect:/patient/login";
        List<Appointment> appts = apptRepo.findByPatientOrderByAppointmentDateTimeDesc(patient);
        List<Doctor> doctors;
        if (specializationFilter != null && !specializationFilter.isBlank()) {
            doctors = doctorRepo.findBySpecializationIgnoreCase(specializationFilter);
        } else {
            doctors = doctorRepo.findAll();
        }
        model.addAttribute("activeTab", "HOME");
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appts);
        model.addAttribute("doctors", doctors);
        model.addAttribute("specializationFilter", specializationFilter == null ? "" : specializationFilter);
        return "patient/patient-home";
    }

    @PostMapping("/appointments")
    public String createAppointment(HttpSession session,
                                    @RequestParam Long doctorId,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                                    @RequestParam String reason) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) return "redirect:/patient/login";
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setAppointmentDateTime(dateTime);
        a.setReason(reason);
        a.setStatus("SCHEDULED");
        apptRepo.save(a);
        return "redirect:/patient/home";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) return "redirect:/patient/login";
        Appointment a = apptRepo.findById(id).orElse(null);
        if (a != null && a.getPatient().getId().equals(patient.getId())) {
            a.setStatus("CANCELLED");
            apptRepo.save(a);
        }
        return "redirect:/patient/home";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id, HttpSession session) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) return "redirect:/patient/login";
        Appointment a = apptRepo.findById(id).orElse(null);
        if (a != null && a.getPatient().getId().equals(patient.getId())) {
            a.setStatus("COMPLETED");
            apptRepo.save(a);
        }
        return "redirect:/patient/home";
    }
}
