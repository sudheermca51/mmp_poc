package com.example.mmp.controller.web;

import com.example.mmp.model.AdminUser;
import com.example.mmp.model.Appointment;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AdminUserRepository;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminPortalController {

    private final AdminUserRepository adminRepo;
    private final AppointmentRepository apptRepo;
    private final PatientRepository patientRepo;

    public AdminPortalController(AdminUserRepository adminRepo,
                                 AppointmentRepository apptRepo,
                                 PatientRepository patientRepo) {
        this.adminRepo = adminRepo;
        this.apptRepo = apptRepo;
        this.patientRepo = patientRepo;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "admin/admin-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        Optional<AdminUser> opt = adminRepo.findByUsername(username)
                .filter(a -> a.getPassword().equals(password));
        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "admin/admin-login";
        }
        session.setAttribute("adminId", opt.get().getId());
        return "redirect:/admin/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        List<Appointment> appts = apptRepo.findAll();
        model.addAttribute("activeTab", "HOME");
        model.addAttribute("appointments", appts);
        model.addAttribute("today", LocalDate.now());
        return "admin/admin-home";
    }

    @GetMapping("/patients")
    public String patients(@RequestParam(required = false) String q,
                           Model model,
                           HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        List<Patient> patients;
        if (q != null && !q.isBlank()) {
            patients = patientRepo
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q);
        } else {
            patients = patientRepo.findAll();
        }
        model.addAttribute("activeTab", "PATIENTS");
        model.addAttribute("patients", patients);
        model.addAttribute("query", q == null ? "" : q);
        return "admin/admin-patients";
    }

    @PostMapping("/patients/{id}/approve")
    public String approvePatient(@PathVariable Long id, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        patientRepo.findById(id).ifPresent(p -> { p.setApproved(true); patientRepo.save(p); });
        return "redirect:/admin/patients";
    }

    @PostMapping("/patients/{id}/reject")
    public String rejectPatient(@PathVariable Long id, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        patientRepo.findById(id).ifPresent(p -> { p.setApproved(false); patientRepo.save(p); });
        return "redirect:/admin/patients";
    }

    @GetMapping("/claim-center")
    public String claimCenter(Model model, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        model.addAttribute("activeTab", "CLAIMS");
        return "admin/admin-claims";
    }
}
