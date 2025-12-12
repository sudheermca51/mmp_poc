package com.example.mmp.controller.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import com.example.mmp.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient/appointments")
public class PatientAppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(PatientAppointmentController.class);

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository apptRepo;

    public PatientAppointmentController(PatientRepository patientRepo,
                                        DoctorRepository doctorRepo,
                                        AppointmentRepository apptRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.apptRepo = apptRepo;
    }

    // Show list page with appointments for logged-in patient
    @GetMapping
    public String list(Model model, HttpSession session) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<Appointment> appts = apptRepo.findByPatientOrderByAppointmentDateTimeDesc(patient);
        model.addAttribute("appointments", appts);
        model.addAttribute("patient", patient);
        model.addAttribute("activeMenu", "schedule");
        return "patient/patient-appointments";
    }

    @GetMapping("/new")
    public String form(Model model, HttpSession session) {
        Patient patient = getLoggedPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        List<Doctor> doctors = doctorRepo.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("patient", patient);
        model.addAttribute("activeMenu", "schedule");
        return "patient/patient-appointment-new";
    }

    /**
     * Create appointment (date-only).
     *
     * Expects form field: name="date" with value "yyyy-MM-dd".
     * Converts to LocalDateTime at start of day for persistence.
     */
    @PostMapping("/new")
    public String createAppointment(HttpSession session,
                                    @RequestParam Long doctorId,
                                    @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    @RequestParam(required = false) String reason,
                                    RedirectAttributes redirectAttrs) {

        // Ensure patient logged in
        Patient patient = getLoggedPatient(session);
        if (patient == null) {
            return "redirect:/patient/login";
        }

        // Validate doctorId presence (defensive)
        if (doctorId == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Please choose a doctor.");
            return "redirect:/patient/appointments/new";
        }

        // Validate date presence
        if (date == null) {
            redirectAttrs.addFlashAttribute("errorMessage", "Please select a date for the appointment.");
            return "redirect:/patient/appointments/new";
        }

        // Date should not be in the past
        if (date.isBefore(LocalDate.now())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Selected date is in the past. Please choose a future date.");
            return "redirect:/patient/appointments/new";
        }

        // Validate doctor exists
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            logger.warn("Attempt to create appointment with invalid doctorId={}", doctorId);
            redirectAttrs.addFlashAttribute("errorMessage", "Selected doctor not found. Please choose a valid doctor.");
            return "redirect:/patient/appointments/new";
        }
        Doctor doctor = doctorOpt.get();

        // Validate reason (optional but trimmed and minimal length)
        String reasonTrimmed = (reason == null) ? "" : reason.trim();
        if (reasonTrimmed.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Please provide a reason for the appointment.");
            return "redirect:/patient/appointments/new";
        }
        // Optional: limit reason length
        if (reasonTrimmed.length() > 1000) {
            redirectAttrs.addFlashAttribute("errorMessage", "Reason is too long (max 1000 characters).");
            return "redirect:/patient/appointments/new";
        }

        // Convert date -> LocalDateTime for persistence (start of day)
        LocalDateTime appointmentDateTime = date.atStartOfDay();

        // Create appointment
        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setAppointmentDateTime(appointmentDateTime);
        a.setReason(reasonTrimmed);
        a.setStatus("SCHEDULED");

        try {
            apptRepo.save(a);
        } catch (Exception ex) {
            logger.error("Failed to save appointment: patientId={}, doctorId={}, date={}, error={}",
                    patient.getId(), doctor.getId(), date, ex.getMessage(), ex);
            redirectAttrs.addFlashAttribute("errorMessage", "Failed to schedule appointment. Please try again later.");
            return "redirect:/patient/appointments/new";
        }

        redirectAttrs.addFlashAttribute("successMessage", "Appointment scheduled for " + date.toString());
        logger.info("Appointment created: patientId={}, doctorId={}, date={}", patient.getId(), doctor.getId(), date);

        return "redirect:/patient/home";
    }

    // Helper to resolve logged-in patient from session
    private Patient getLoggedPatient(HttpSession session) {
        Object obj = session.getAttribute("loggedInUser");
        if (obj == null) return null;

        if (obj instanceof Patient) {
            return (Patient) obj;
        }

        if (obj instanceof String) {
            String username = ((String) obj).trim();
            return patientRepo.findByUsername(username).orElse(null);
        }

        return null;
    }
}
