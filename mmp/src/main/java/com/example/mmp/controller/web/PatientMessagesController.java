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
@RequestMapping("/patient/messages")
public class PatientMessagesController {

    private final PatientRepository patientRepo;
    private final MessageRepository messageRepo;

    public PatientMessagesController(PatientRepository patientRepo, MessageRepository messageRepo) {
        this.patientRepo = patientRepo;
        this.messageRepo = messageRepo;
    }

    private Patient getLoggedPatient(HttpSession session) {
        Object idObj = session.getAttribute("patientId");
        if (idObj == null) return null;
        Long id = (Long) idObj;
        return patientRepo.findById(id).orElse(null);
    }

    @GetMapping
    public String messages(Model model, HttpSession session) {
        Patient p = getLoggedPatient(session);
        if (p == null) return "redirect:/patient/login";
        List<Message> messages = messageRepo.findByPatientOrderByCreatedAtDesc(p);
        model.addAttribute("patient", p);
        model.addAttribute("messages", messages);
        return "patient/patient-messages";
    }

    @PostMapping
    public String send(@RequestParam String text, HttpSession session) {
        Patient p = getLoggedPatient(session);
        if (p == null) return "redirect:/patient/login";
        Message m = new Message();
        m.setPatient(p);
        m.setSenderRole("PATIENT");
        m.setText(text);
        m.setStatus("OPEN");
        messageRepo.save(m);
        return "redirect:/patient/messages";
    }
}
