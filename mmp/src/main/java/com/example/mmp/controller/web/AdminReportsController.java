package com.example.mmp.controller.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Fee;
import com.example.mmp.model.Report;
import com.example.mmp.model.ReportType;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.FeeRepository;
import com.example.mmp.repository.PatientRepository;
import com.example.mmp.repository.ReportRepository;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/admin/reports")
public class AdminReportsController {

	private final AppointmentRepository apptRepo;
	private final FeeRepository feeRepo;
	private final PatientRepository patientRepo;
	private final ReportRepository reportRepo;


 

	public AdminReportsController(AppointmentRepository apptRepo,
			FeeRepository feeRepo,
			PatientRepository patientRepo,
			ReportRepository reportRepo) {
		this.apptRepo = apptRepo;
		this.feeRepo = feeRepo;
		this.patientRepo = patientRepo;
		this.reportRepo = reportRepo;
	}

	private boolean isLogged(HttpSession session) {
		return session.getAttribute("adminId") != null;
	}

	/**
	 * REPORTS DASHBOARD
	 * URL: /admin/reports
	 */
	@GetMapping
	public String reports(Model model, HttpSession session) {
		if (!isLogged(session)) return "redirect:/admin/login";

		long totalAppointments = apptRepo.count();
		long totalFees = feeRepo.count();

		BigDecimal totalAmount = feeRepo.findAll().stream()
				.map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getAmount())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		long paidCount = feeRepo.findAll().stream()
				.filter(f -> f.isPaid())
				.count();
		 model.addAttribute("activeMenu", "reports");
	        model.addAttribute("activeTab", "reports");
		model.addAttribute("totalAppointments", totalAppointments);
		model.addAttribute("totalFees", totalFees);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("paidCount", paidCount);
		return "admin/admin-reports";
	}

	/**
	 * UPLOAD REPORT PAGE
	 * URL: /admin/reports/upload
	 */


	@GetMapping("/upload")
	public String uploadReportForm(
			@RequestParam(required = false) Long patientId,
			Model model,
			HttpSession session) {

		if (!isLogged(session)) return "redirect:/admin/login";

		model.addAttribute("patients", patientRepo.findAll());
		model.addAttribute("appointments", List.of());

		// 🔴 ADD THIS LINE
		model.addAttribute("selectedPatientId", patientId);

		if (patientId != null) {
			model.addAttribute("appointments",
					apptRepo.findByPatientId(patientId));
		}

		return "admin/admin-report-upload";
	}
	@PostMapping("/upload")
	public String uploadReport(
	        @RequestParam Long patientId,
	        @RequestParam Long appointmentId,
	        @RequestParam String reportType,
	        @RequestParam BigDecimal feeAmount,
	        @RequestParam("file") MultipartFile file,
	        HttpSession session,
	        RedirectAttributes ra) throws IOException {

	    if (!isLogged(session)) return "redirect:/admin/login";

	    Appointment appt = apptRepo.findById(appointmentId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid appointment"));

	    // 🔒 Safety check
	    if (!appt.getPatient().getId().equals(patientId)) {
	        ra.addFlashAttribute("error", "Invalid appointment selection");
	        return "redirect:/admin/reports/upload?patientId=" + patientId;
	    }

	    /* --------------------
	       1️⃣ SAVE REPORT
	       -------------------- */
	    Path uploadDir = Paths.get("uploads/reports");
	    Files.createDirectories(uploadDir);

	    String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	    Path target = uploadDir.resolve(storedName);
	    Files.copy(file.getInputStream(), target);

	    Report report = new Report();
	    report.setAppointment(appt);
	    report.setFileName(file.getOriginalFilename());
	    report.setContentType(file.getContentType());
	    report.setStoragePath(target.toString());
	    reportRepo.save(report);

	    /* --------------------
	       2️⃣ SAVE FEE
	       -------------------- */
	    ReportType type = ReportType.valueOf(reportType);

	    Fee fee = new Fee();
	    fee.setAppointment(appt);
	    fee.setAmount(feeAmount);
	    fee.setDescription(type.getLabel());
	    fee.setPaid(false);
	    feeRepo.save(fee);

	    ra.addFlashAttribute("success",
	            "Report uploaded and fee added successfully");

	    return "redirect:/admin/reports";
	}


}
