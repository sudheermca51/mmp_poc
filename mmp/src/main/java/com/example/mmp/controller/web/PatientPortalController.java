package com.example.mmp.controller.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mmp.model.Patient;
import com.example.mmp.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient")
public class PatientPortalController {


    private final PatientRepository patientRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public PatientPortalController(PatientRepository patientRepo,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.patientRepo = patientRepo;
        this.passwordEncoder = passwordEncoder;
    }

	@GetMapping("/login")
	public String loginForm() {
		return "patient/patient-login";
	}

	@PostMapping("/doLogin")
	public String doLogin(@RequestParam String username,
			@RequestParam String password,
			HttpSession session,
			Model model) {

		String user = username == null ? "" : username.trim();
		String pass = password == null ? "" : password.trim();

		if (user == null || user.isBlank() || pass == null) {
			model.addAttribute("error", "Invalid credentials");
			return "patient/patient-login";
		}

		Optional<Patient> opt = patientRepo.findByUsername(user);
		if (opt.isEmpty()) {
			model.addAttribute("error", "Invalid username or password");
			return "patient/patient-login";
		}

		Patient p = opt.get();

		if (Boolean.TRUE.equals(p.isRejected())) {
			model.addAttribute("error", "Your registration was rejected");
			return "patient/patient-login";
		}

		if (!Boolean.TRUE.equals(p.isApproved())) {
			model.addAttribute("error", "Your account is not yet approved by admin");
			return "patient/patient-login";
		}

		// Correct password check
		String stored = p.getPassword() == null ? "" : p.getPassword();
		
		 
		boolean passwordOk = false;

	    // If stored value looks like a BCrypt hash, use the encoder
	    if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
	        passwordOk = passwordEncoder.matches(pass, stored);
	    } else {
	        // fallback for legacy plain-text passwords
	        passwordOk = pass.equals(stored);
	    }

	    if (!passwordOk) {
	        model.addAttribute("error", "Invalid username or password");
	        return "patient/patient-login";
	    }


		// Login success
		session.setAttribute("loggedInUser", p.getUsername());
	 	return "redirect:/patient/home";
	}


	@GetMapping("/register")
	public String registerForm() {
		return "patient/patient-register";
	}

	@PostMapping("/register")
	public String doRegister(@RequestParam String username,
			@RequestParam String password,
			@RequestParam String firstName,
			@RequestParam(required = false) String lastName,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String gender,
			@RequestParam(required = false) String dob,
			@RequestParam(required = false) String address,
			Model model) {

		if (patientRepo.findByUsername(username).isPresent()) {
			model.addAttribute("error", "Username already exists");
			return "patient/patient-register";
		}

		Patient p = new Patient();
		p.setUsername(username);
		p.setPassword(password); // keeping your existing logic
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setEmail(email);

		// new fields
		p.setPhone(phone);
		p.setGender(gender);
		p.setDob(dob);          // adjust type if your field is Date/LocalDate
		p.setAddress(address);

		p.setApproved(false);
		p.setRejected(false);
		patientRepo.save(p);

		model.addAttribute("message", "Registration submitted. Wait for admin approval.");
		return "patient/patient-login";
	}



	private String resolveUsername(HttpSession session) {
		Object obj = session.getAttribute("loggedInUser");

		if (obj == null) return null;

		// Sometimes you store just the username (String)
		if (obj instanceof String) {
			return (String) obj;
		}

		// Sometimes you might be storing the full Patient object
		if (obj instanceof Patient) {
			return ((Patient) obj).getUsername();
		}

		return null;
	}

	// GET: show edit form (pre-fill patientDto)
	@GetMapping("/profile")
	public String showProfile(Model model, HttpSession session) {
		String username = resolveUsername(session);
		if (username == null) return "redirect:/patient/login";

		Patient p = patientRepo.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException("User not found"));

		// Do not include actual password in the form
		p.setPassword(null);

		model.addAttribute("patientDto", p);
		model.addAttribute("activeMenu", "profile");
		return "patient/patient-edit";
	}

	// POST: update profile (binds to patientDto)
	@PostMapping("/profile")
	public String updateProfile(@ModelAttribute("patientDto") Patient dto,
			@RequestParam(value = "photo", required = false) MultipartFile photo,
			HttpSession session,
			Model model,
			RedirectAttributes ra) {

		String loggedIn = resolveUsername(session);
		if (loggedIn == null) return "redirect:/patient/login";

		Patient existing = patientRepo.findByUsername(loggedIn)
				.orElseThrow(() -> new IllegalStateException("User not found"));

		// username uniqueness check (only when changed)
		if (dto.getUsername() != null && !dto.getUsername().equals(existing.getUsername())) {
			if (patientRepo.findByUsername(dto.getUsername()).isPresent()) {
				model.addAttribute("patientDto", existing);
				model.addAttribute("usernameError", "Username already exists");
				return "patient/patient-edit";
			}
			existing.setUsername(dto.getUsername());
			// update session if username stored there
			Object sessUser = session.getAttribute("loggedInUser");
			if (sessUser instanceof String) session.setAttribute("loggedInUser", dto.getUsername());
			else if (sessUser instanceof Patient) {
				((Patient)sessUser).setUsername(dto.getUsername());
				session.setAttribute("loggedInUser", sessUser);
			}
		}

		// WHITELIST: update only allowed fields
		existing.setFirstName(dto.getFirstName());
		existing.setLastName(dto.getLastName());
		existing.setEmail(dto.getEmail());
		existing.setPhone(dto.getPhone());
		existing.setGender(dto.getGender());
		existing.setDob(dto.getDob());
		existing.setAddress(dto.getAddress());

		// password: only update when provided (non-empty)
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			existing.setPassword(dto.getPassword()); // matches your current raw-password behavior
			// If you plan to migrate to hashed passwords later, do it in a controlled migration.
		}

		// Photo handling (optional): save file and set photo path
		if (photo != null && !photo.isEmpty()) {
			try {
				// save to a folder, example; adjust path as needed
				String uploadsDir = "uploads/patients/";
				Files.createDirectories(Paths.get(uploadsDir));
				String filename = "patient-" + existing.getId() + "-" + System.currentTimeMillis() + "-" + photo.getOriginalFilename();
				Path filePath = Paths.get(uploadsDir).resolve(filename);
				Files.write(filePath, photo.getBytes());
				existing.setPhotoPath(filePath.toString()); // store path or URL in DB
			} catch (IOException ex) {
				model.addAttribute("error", "Failed to save photo");
				model.addAttribute("patientDto", dto);
				return "patient/patient-edit";
			}
		}

		patientRepo.save(existing);

		ra.addFlashAttribute("success", "Profile updated successfully");
		return "redirect:/patient/profile";
	}


	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/patient/login";
	}
}
