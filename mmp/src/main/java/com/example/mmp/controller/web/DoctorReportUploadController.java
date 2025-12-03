
package com.example.mmp.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor/reports")
public class DoctorReportUploadController {
 @GetMapping
 public String uploadPage(){ return "doctor/doctor-report-upload"; }
 @PostMapping
 public String upload(){ return "redirect:/doctor/home"; }
}
