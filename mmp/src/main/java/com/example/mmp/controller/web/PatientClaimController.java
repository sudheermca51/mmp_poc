package com.example.mmp.controller.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mmp.model.Claim;
import com.example.mmp.model.Fee;
import com.example.mmp.repository.ClaimRepository;
import com.example.mmp.repository.FeeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient/claims")
public class PatientClaimController {

    private final ClaimRepository claimRepo;
    private final FeeRepository feeRepo;

    public PatientClaimController(ClaimRepository claimRepo,
                                  FeeRepository feeRepo) {
        this.claimRepo = claimRepo;
        this.feeRepo = feeRepo;
    }

    private Long getPatientId(HttpSession session) {
        return (Long) session.getAttribute("patientId");
    }

    /* -----------------------------
       SUBMIT CLAIM
       ----------------------------- */
    @PostMapping("/submit/{feeId}")
    public String submitClaim(@PathVariable Long feeId,
                              HttpSession session,
                              RedirectAttributes ra) {

        Long patientId = getPatientId(session);
        if (patientId == null) {
            return "redirect:/patient/login";
        }

        Fee fee = feeRepo.findById(feeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 🔒 Ownership check
        if (!fee.getAppointment().getPatient().getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // ❌ Fee not paid → cannot claim
        if (!fee.isPaid()) {
            ra.addFlashAttribute("error", "Fee must be paid before submitting claim");
            return "redirect:/patient/fees";
        }

        // ❌ Claim already exists
        if (claimRepo.findByFeeId(feeId).isPresent()) {
            ra.addFlashAttribute("error", "Claim already submitted");
            return "redirect:/patient/fees";
        }

        // ✅ Create claim
        Claim claim = new Claim();
        claim.setPatient(fee.getAppointment().getPatient());
        claim.setAppointment(fee.getAppointment());
        claim.setFee(fee);
        claim.setClaimAmount(fee.getAmount());
        claim.setStatus("SUBMITTED");

        claimRepo.save(claim);

        ra.addFlashAttribute("success", "Claim submitted successfully");

        return "redirect:/patient/fees";
    }
}
