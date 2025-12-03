package com.example.mmp.controller.web;

import com.example.mmp.model.Message;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.MessageRepository;
import com.example.mmp.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/messages")
public class AdminMessagesController {

    private final MessageRepository messageRepo;
    private final PatientRepository patientRepo;

    public AdminMessagesController(MessageRepository messageRepo, PatientRepository patientRepo) {
        this.messageRepo = messageRepo;
        this.patientRepo = patientRepo;
    }

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    @GetMapping
    public String messages(Model model, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        List<Message> messages = messageRepo.findAllByOrderByCreatedAtDesc();
        model.addAttribute("messages", messages);
        return "admin/admin-messages";
    }

    @PostMapping("/reply")
    public String reply(@RequestParam Long patientId,
                        @RequestParam String text,
                        HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";
        Patient p = patientRepo.findById(patientId).orElse(null);
        if (p == null) return "redirect:/admin/messages";
        Message m = new Message();
        m.setPatient(p);
        m.setSenderRole("ADMIN");
        m.setText(text);
        m.setStatus("OPEN");
        messageRepo.save(m);
        return "redirect:/admin/messages";
    }
}
