package com.example.mmp.controller.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.mmp.model.Fee;
import com.example.mmp.repository.FeeRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/patient/fees")
public class PatientFeesController {

    private final FeeRepository feeRepo;

    public PatientFeesController(FeeRepository feeRepo) {
        this.feeRepo = feeRepo;
    }

    private Long getPatientId(HttpSession session) {
        return (Long) session.getAttribute("patientId");
    }

    /* ----------------------------
       VIEW FEES
       ---------------------------- */
    @GetMapping
    public String viewFees(Model model, HttpSession session) {

        Long patientId = getPatientId(session);
        if (patientId == null) {
            return "redirect:/patient/login";
        }

        List<Fee> fees = feeRepo.findByAppointmentPatientId(patientId);
        model.addAttribute("fees", fees);

        return "patient/patient-fees";
    }

    /* ----------------------------
       PAY FEE (FORM)
       ---------------------------- */
    @GetMapping("/pay/{feeId}")
    public String payFee(@PathVariable Long feeId,
                         Model model,
                         HttpSession session) {

        Long patientId = getPatientId(session);
        if (patientId == null) {
            return "redirect:/patient/login";
        }

        Fee fee = feeRepo.findById(feeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 🔒 ownership check
        if (!fee.getAppointment().getPatient().getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("fee", fee);
        return "patient/fee-pay";
    }

    /* ----------------------------
       CONFIRM PAYMENT (DUMMY)
       ---------------------------- */
    @PostMapping("/pay/{feeId}")
    public String confirmPayment(@PathVariable Long feeId,
                                 HttpSession session) {

        Long patientId = getPatientId(session);
        if (patientId == null) {
            return "redirect:/patient/login";
        }

        Fee fee = feeRepo.findById(feeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!fee.getAppointment().getPatient().getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        fee.setPaid(true);
        fee.setPaidAt(LocalDateTime.now());
        feeRepo.save(fee);

        return "redirect:/patient/fees";
    }
}
