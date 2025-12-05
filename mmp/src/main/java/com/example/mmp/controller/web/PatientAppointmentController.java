package com.example.mmp.controller.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mmp.model.Appointment;
import com.example.mmp.model.Doctor;
import com.example.mmp.model.Patient;
import com.example.mmp.repository.AppointmentRepository;
import com.example.mmp.repository.DoctorRepository;
import com.example.mmp.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient/appointments")
public class PatientAppointmentController {

	private final PatientRepository patientRepo;
	private final DoctorRepository doctorRepo;
	private final AppointmentRepository apptRepo;

	public PatientAppointmentController(PatientRepository patientRepo,DoctorRepository doctorRepo,
			AppointmentRepository apptRepo) {
		this.doctorRepo = doctorRepo;
		this.apptRepo = apptRepo;
		this.patientRepo = patientRepo;
	}

	// show list page with appointments for logged-in patient
	@GetMapping
	public String list(Model model, HttpSession session) {
		Patient patient = getLoggedPatient(session);            // implement same helper as other controllers
		if (patient == null) return "redirect:/patient/login";

		List<Appointment> appts = apptRepo.findByPatientOrderByAppointmentDateTimeDesc(patient);
		model.addAttribute("appointments", appts);
		model.addAttribute("patient", patient);                // for sidebar/header
		model.addAttribute("activeMenu", "schedule");          // highlights Schedule link
		return "patient/patient-appointments";
	}

	@GetMapping("/new")
	public String form(Model model, HttpSession session) {
		Patient patient = getLoggedPatient(session);
		if (patient == null) return "redirect:/patient/login";

		List<Doctor> doctors = doctorRepo.findAll();
		model.addAttribute("doctors", doctors);
		model.addAttribute("patient", patient);
		model.addAttribute("activeMenu", "schedule");
		return "patient/patient-appointment-new";
	}
	
	@PostMapping("/new")
	public String createAppointment(HttpSession session,
	        @RequestParam Long doctorId,
	        @RequestParam("dateTime") String dateTimeStr,
	        @RequestParam String reason) {

	    Patient patient = getLoggedPatient(session);
	    if (patient == null) return "redirect:/patient/login";

	    Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();

	    // Try parsing common formats: ISO first (yyyy-MM-dd'T'HH:mm) then space format (yyyy-MM-dd HH:mm)
	    LocalDateTime dateTime;
	    DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME;          // "yyyy-MM-dd'T'HH:mm[:ss]"
	    DateTimeFormatter space = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	    try {
	        dateTime = LocalDateTime.parse(dateTimeStr, iso);
	    } catch (DateTimeParseException ex) {
	        // fallback to space-separated format
	        dateTime = LocalDateTime.parse(dateTimeStr, space);
	    }

	    Appointment a = new Appointment();
	    a.setPatient(patient);
	    a.setDoctor(doctor);
	    a.setAppointmentDateTime(dateTime);
	    a.setReason(reason);
	    a.setStatus("SCHEDULED");
	    apptRepo.save(a);
	    return "redirect:/patient/home";
	}


//	@PostMapping("/new")
//	public String createAppointment(HttpSession session,
//			@RequestParam Long doctorId,
//			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
//			@RequestParam String reason) {
//		Patient patient = getLoggedPatient(session);
//		if (patient == null) return "redirect:/patient/login";
//		Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
//		Appointment a = new Appointment();
//		a.setPatient(patient);
//		a.setDoctor(doctor);
//		a.setAppointmentDateTime(dateTime);
//		a.setReason(reason);
//		a.setStatus("SCHEDULED");
//		apptRepo.save(a);
//		return "redirect:/patient/home";
//	}
	//    public String save(@RequestParam Long doctorId,
	//                       @RequestParam String dateTime,    // adjust to LocalDateTime binding if needed
	//                       @RequestParam String reason,
	//                       HttpSession session) {
	//    	  Patient patient = getLoggedPatient(session);
	//          if (patient == null) return "redirect:/patient/login";
	//          Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
	//          Appointment a = new Appointment();
	//          a.setPatient(patient);
	//          a.setDoctor(doctor);
	//          a.setAppointmentDateTime(dateTime);
	//          a.setReason(reason);
	//          a.setStatus("SCHEDULED");
	//          apptRepo.save(a);
	//          return "redirect:/patient/home";
	//    }
	
	private Patient getLoggedPatient(HttpSession session) {
		Object obj = session.getAttribute("loggedInUser");
		if (obj == null) return null;

		// If you already stored the Patient object
		if (obj instanceof Patient) {
			return (Patient) obj;
		}

		// If you stored username string (recommended)
		if (obj instanceof String) {
			String username = ((String) obj).trim();
			return patientRepo.findByUsername(username).orElse(null);
		}

		return null;
	}
}
