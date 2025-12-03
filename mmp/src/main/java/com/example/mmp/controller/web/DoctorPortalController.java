package com.example.mmp.controller.web;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorPortalController {

    private final DoctorRepository doctorRepo;
    private final AppointmentRepository apptRepo;

    public DoctorPortalController(DoctorRepository doctorRepo, AppointmentRepository apptRepo) {
        this.doctorRepo = doctorRepo;
        this.apptRepo = apptRepo;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("doctors", doctorRepo.findAll());
        return "doctor/doctor-login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam Long doctorId, HttpSession session) {
        Optional<Doctor> opt = doctorRepo.findById(doctorId);
        if (opt.isEmpty()) return "redirect:/doctor/login?error";
        session.setAttribute("doctorId", opt.get().getId());
        return "redirect:/doctor/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/doctor/login";
    }

    private Doctor getLoggedDoctor(HttpSession session) {
        Object idObj = session.getAttribute("doctorId");
        if (idObj == null) return null;
        Long id = (Long) idObj;
        return doctorRepo.findById(id).orElse(null);
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Doctor d = getLoggedDoctor(session);
        if (d == null) return "redirect:/doctor/login";
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();
        List<Appointment> todays = apptRepo
                .findByDoctorAndAppointmentDateTimeBetweenOrderByAppointmentDateTimeAsc(d, from, to);
        model.addAttribute("doctor", d);
        model.addAttribute("today", today);
        model.addAttribute("appointments", todays);
        return "doctor/doctor-home";
    }
}
