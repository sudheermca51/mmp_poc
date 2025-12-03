
package com.example.mmp.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/patient/appointments")
public class PatientAppointmentController {
 @GetMapping
 public String list(){ return "patient/patient-appointments"; }
 @GetMapping("/new")
 public String form(){ return "patient/patient-appointment-new"; }
 @PostMapping("/new")
 public String save(){ return "redirect:/patient/appointments"; }
}
