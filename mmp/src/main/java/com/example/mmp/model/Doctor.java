package com.example.mmp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String specialization;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getSpecialization() { return specialization; }
	public void setSpecialization(String specialization) { this.specialization = specialization; }

	// existing fields...
	private String photoPath; // e.g. "doctor3.jpg" or "/images/doctors/doctor3.jpg"

	public String getPhotoPath() { return photoPath; }
	public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }



}
