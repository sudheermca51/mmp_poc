package com.example.mmp.controller.web;

import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.FeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportsController {

    private final AppointmentRepository apptRepo;
    private final FeeRepository feeRepo;

    public AdminReportsController(AppointmentRepository apptRepo, FeeRepository feeRepo) {
        this.apptRepo = apptRepo;
        this.feeRepo = feeRepo;
    }

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    @GetMapping
    public String reports(Model model, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";

        long totalAppointments = apptRepo.count();
        long totalFees = feeRepo.count();
        BigDecimal totalAmount = feeRepo.findAll().stream()
                .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidCount = feeRepo.findAll().stream().filter(f -> f.isPaid()).count();

        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("totalFees", totalFees);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paidCount", paidCount);
        return "admin/admin-reports";
    }
}
