package com.example.mmp.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Fee;
import com.example.mmp.model.Patient;
import com.example.mmp.model.Report;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import com.example.mmp.repository.FeeRepository;
import com.example.mmp.repository.ReportRepository;

import jakarta.servlet.http.HttpSession;
@Controller
@RequestMapping("/doctor")
public class DoctorPortalController {

	private final DoctorRepository doctorRepo;
	private final AppointmentRepository apptRepo;
	private final FeeRepository feeRepo;
	private final ReportRepository reportRepo;

	public DoctorPortalController(DoctorRepository doctorRepo,
			AppointmentRepository apptRepo,
			FeeRepository feeRepo,
			ReportRepository reportRepo)
	{
		this.doctorRepo = doctorRepo;
		this.apptRepo = apptRepo;
		this.feeRepo = feeRepo;
		this.reportRepo = reportRepo;
	}


	/* ---------------- LOGIN ---------------- */

	@GetMapping("/login")
	public String loginForm() {
		return "doctor/doctor-login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam String username,
			@RequestParam String password,
			HttpSession session,
			Model model) {

		Doctor doctor = doctorRepo.findByUsername(username).orElse(null);

		if (doctor == null || !doctor.getPassword().equals(password)) {
			model.addAttribute("error", "Invalid credentials");
			return "doctor/doctor-login";
		}

		session.setAttribute("doctorId", doctor.getId());
		session.setAttribute("doctorName", doctor.getName());

		return "redirect:/doctor/home";
	}

	/* ---------------- HOME ---------------- */

	@GetMapping("/home")
	public String home(Model model, HttpSession session) {

		Long doctorId = (Long) session.getAttribute("doctorId");
		if (doctorId == null) {
			return "redirect:/doctor/login";
		}

		Doctor doctor = doctorRepo.findById(doctorId)
				.orElseThrow(() -> new IllegalStateException("Doctor not found"));

		// ✅ IMPORTANT FIX HERE
		List<Appointment> appointments = apptRepo.findByDoctorId(doctorId);

		model.addAttribute("doctor", doctor);
		model.addAttribute("appointments", appointments);

		return "doctor/doctor-home";
	}


	/* ---------------- LOGOUT ---------------- */

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/doctor/login";
	}
	@GetMapping("/appointment/{id}/history")
	public String viewPatientHistory(
			@PathVariable Long id,
			Model model,
			HttpSession session) {

		Doctor doctor = getLoggedDoctor(session);
		if (doctor == null) return "redirect:/doctor/login";

		Appointment appt = apptRepo.findById(id)
				.orElseThrow();

		// 🔐 Security check
		if (!appt.getDoctor().getId().equals(doctor.getId())) {
			return "redirect:/doctor/home";
		}

		Patient patient = appt.getPatient();

		// ✅ ALWAYS initialize lists
		List<Appointment> pastAppointments =
				apptRepo.findByPatientId(patient.getId())
				.stream()
				.filter(a -> !a.getId().equals(appt.getId()))
				.toList();

		List<Report> reports =
				reportRepo.findByAppointmentPatientId(patient.getId());

		List<Fee> fees =
				feeRepo.findByAppointmentPatientId(patient.getId());

		// ✅ ADD ALL ATTRIBUTES
		model.addAttribute("patient", patient);
		model.addAttribute("appointment", appt);
		model.addAttribute("pastAppointments", pastAppointments);
		model.addAttribute("reports", reports);
		model.addAttribute("fees", fees);

		return "doctor/patient-history";
	}
	private Doctor getLoggedDoctor(HttpSession session) {
		Object idObj = session.getAttribute("doctorId");
		if (idObj == null) {
			return null;
		}
		Long doctorId = (Long) idObj;
		return doctorRepo.findById(doctorId).orElse(null);
	}

}
