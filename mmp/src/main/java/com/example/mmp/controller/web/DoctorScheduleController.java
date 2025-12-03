
package com.example.mmp.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/doctor/schedule")
public class DoctorScheduleController {
 @GetMapping
 public String today(){ return "doctor/doctor-schedule"; }
}
