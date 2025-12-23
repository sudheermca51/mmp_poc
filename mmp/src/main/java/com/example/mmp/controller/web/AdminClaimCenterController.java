package com.example.mmp.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.mmp.model.Claim;
import com.example.mmp.repository.ClaimRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/claim-center")
public class AdminClaimCenterController {

    private final ClaimRepository claimRepo;

    public AdminClaimCenterController(ClaimRepository claimRepo) {
        this.claimRepo = claimRepo;
    }

    private boolean isAdminLoggedIn(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    /* -------------------------
       VIEW CLAIMS
       ------------------------- */
    @GetMapping
    public String viewClaims(Model model, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        List<Claim> claims = claimRepo.findAll();
        model.addAttribute("claims", claims == null ? List.of() : claims);

        return "admin/admin-claims";
    }

    /* -------------------------
       APPROVE CLAIM
       ------------------------- */
    @PostMapping("/{id}/approve")
    public String approveClaim(@PathVariable Long id, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Claim claim = claimRepo.findById(id)
                .orElseThrow();

        claim.setStatus("APPROVED");
        claimRepo.save(claim);

        return "redirect:/admin/claim-center";
    }

    /* -------------------------
       REJECT CLAIM
       ------------------------- */
    @PostMapping("/{id}/reject")
    public String rejectClaim(@PathVariable Long id, HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Claim claim = claimRepo.findById(id)
                .orElseThrow();

        claim.setStatus("REJECTED");
        claimRepo.save(claim);

        return "redirect:/admin/claim-center";
    }
}
