package com.example.mmp.controller.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mmp.model.AdminUser;
import com.example.mmp.model.Appointment;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AdminUserRepository;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

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
    public String patients(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "username") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Patient> patientPage;

        if ("approved".equalsIgnoreCase(status)) {
            patientPage = patientRepo.findByApprovedTrue(pageable);
        } else if ("rejected".equalsIgnoreCase(status)) {
            patientPage = patientRepo.findByRejectedTrue(pageable);
        } else {
            patientPage = patientRepo.findAll(pageable);
        }

        model.addAttribute("patients", patientPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", patientPage.getTotalPages());
        model.addAttribute("totalItems", patientPage.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir",
                sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("status", status);

        return "admin/admin-patients";
    }


    /* ================= APPROVE PATIENT (UPDATED) ================= */

    @PostMapping("/patients/{id}/approve")
    public String approvePatient(@PathVariable Long id, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";

        patientRepo.findById(id).ifPresent(p -> {
            p.setApproved(true);
            p.setRejected(false);   // ✅ ensure consistency
            patientRepo.save(p);
        });

        return "redirect:/admin/patients";
    }

    /* ================= REJECT PATIENT (UPDATED) ================= */

    @PostMapping("/patients/{id}/reject")
    public String rejectPatient(@PathVariable Long id, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";

        patientRepo.findById(id).ifPresent(p -> {
            p.setApproved(false);
            p.setRejected(true);    // ✅ ensure consistency
            patientRepo.save(p);
        });

        return "redirect:/admin/patients";
    }
    @GetMapping("/users")
    public String listAdminUsers(
            @RequestParam(defaultValue = "username") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        List<AdminUser> users = adminRepo.findAll(sort);

        model.addAttribute("users", users);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir",
                sortDir.equals("asc") ? "desc" : "asc");

        return "admin/admin-users";
    }

   
}
