
package com.example.mmp.controller.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/admin/approvals")
public class AdminApprovalController {
 @GetMapping
 public String list(){ return "admin/admin-approvals"; }
 @PostMapping("/approve/{id}")
 public String approve(){ return "redirect:/admin/approvals"; }
 @PostMapping("/reject/{id}")
 public String reject(){ return "redirect:/admin/approvals"; }
}
