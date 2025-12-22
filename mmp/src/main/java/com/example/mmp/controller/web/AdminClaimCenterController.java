package com.example.mmp.controller.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
public class AdminClaimCenterController {

    @GetMapping
    public String viewClaims(Model model, HttpSession session) {
        return "admin/admin-claims";
    }
}