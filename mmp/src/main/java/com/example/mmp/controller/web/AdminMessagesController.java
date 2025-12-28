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

    public AdminMessagesController(MessageRepository messageRepo,
                                   PatientRepository patientRepo) {
        this.messageRepo = messageRepo;
        this.patientRepo = patientRepo;
    }

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    /* ================= INBOX ================= */

    @GetMapping
    public String inbox(Model model, HttpSession session) {
        if (!isLogged(session)) return "redirect:/admin/login";

        model.addAttribute("patients", messageRepo.findDistinctPatients());
        model.addAttribute("activeMenu","Messages");
        return "admin/admin-messages-inbox";
    }

    /* ================= CONVERSATION ================= */

    @GetMapping("/{patientId}")
    public String conversation(@PathVariable Long patientId,
                               Model model,
                               HttpSession session) {

        if (!isLogged(session)) return "redirect:/admin/login";

        Patient patient = patientRepo.findById(patientId).orElse(null);
        if (patient == null) return "redirect:/admin/messages";

        List<Message> messages =
                messageRepo.findByPatientOrderByCreatedAtDesc(patient);

        model.addAttribute("patient", patient);
        model.addAttribute("messages", messages);

        return "admin/admin-messages-conversation";
    }

    /* ================= REPLY ================= */

    @PostMapping("/reply")
    public String reply(@RequestParam Long patientId,
                        @RequestParam String text,
                        HttpSession session) {

        if (!isLogged(session)) return "redirect:/admin/login";

        Patient patient = patientRepo.findById(patientId).orElse(null);
        if (patient == null) return "redirect:/admin/messages";

        // Move OPEN → IN_PROGRESS
        List<Message> openMessages =
                messageRepo.findByPatientAndStatus(patient, "OPEN");

        for (Message m : openMessages) {
            m.setStatus("IN_PROGRESS");
        }
        messageRepo.saveAll(openMessages);

        Message reply = new Message();
        reply.setPatient(patient);
        reply.setSenderRole("ADMIN");
        reply.setText(text);
        reply.setStatus("IN_PROGRESS");
        messageRepo.save(reply);

        return "redirect:/admin/messages/" + patientId;
    }

    /* ================= CLOSE TICKET (FINAL FIX) ================= */

    @PostMapping("/close")
    public String close(@RequestParam Long patientId,
                        HttpSession session) {

        if (!isLogged(session)) return "redirect:/admin/login";

        Patient patient = patientRepo.findById(patientId).orElse(null);
        if (patient == null) return "redirect:/admin/messages";

        List<Message> messages =
                messageRepo.findByPatientOrderByCreatedAtDesc(patient);

        for (Message m : messages) {
            m.setStatus("CLOSED");
        }

        // 🔥 THIS WAS THE MISSING PIECE
        messageRepo.saveAll(messages);

        return "redirect:/admin/messages/" + patientId;
    }
}
