package com.example.mmp.controller.web;

import com.example.mmp.model.Patient;
import com.example.mmp.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientPortalController {

    private final PatientRepository patientRepo;

    public PatientPortalController(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "patient/patient-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        Optional<Patient> opt = patientRepo.findByUsername(username)
                .filter(p -> p.getPassword().equals(password) && p.isApproved());
        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid username/password or not approved by admin yet");
            return "patient/patient-login";
        }
        session.setAttribute("patientId", opt.get().getId());
        return "redirect:/patient/home";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "patient/patient-register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String firstName,
                             @RequestParam(required = false) String lastName,
                             @RequestParam(required = false) String email,
                             Model model) {
        if (patientRepo.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already exists");
            return "patient/patient-register";
        }
        Patient p = new Patient();
        p.setUsername(username);
        p.setPassword(password);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setEmail(email);
        p.setApproved(false);
        patientRepo.save(p);
        model.addAttribute("message", "Registration submitted. Wait for admin approval.");
        return "patient/patient-login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/patient/login";
    }
}
