package com.example.mmp.config;

import com.example.mmp.model.*;
import com.example.mmp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(PatientRepository patientRepo,
                               DoctorRepository doctorRepo,
                               AppointmentRepository apptRepo,
                               AdminUserRepository adminRepo,
                               FeeRepository feeRepo) {
        return args -> {

            adminRepo.findByUsername("admin1").orElseGet(() -> {
                AdminUser a = new AdminUser();
                a.setUsername("admin1");
                a.setPassword("admin123");
                a.setFullName("Portal Admin");
                return adminRepo.save(a);
            });

            if (doctorRepo.count() == 0) {
                Doctor d1 = new Doctor();
                d1.setName("Dr. Arjun Heart");
                d1.setSpecialization("Cardiologist");
                doctorRepo.save(d1);

                Doctor d2 = new Doctor();
                d2.setName("Dr. Meera Skin");
                d2.setSpecialization("Dermatologist");
                doctorRepo.save(d2);

                Doctor d3 = new Doctor();
                d3.setName("Dr. Ravi Ortho");
                d3.setSpecialization("Orthopedic");
                doctorRepo.save(d3);
            }

            patientRepo.findByUsername("patient1").orElseGet(() -> {
                Patient p = new Patient();
                p.setUsername("patient1");
                p.setPassword("password");
                p.setFirstName("Demo");
                p.setLastName("Patient");
                p.setEmail("patient1@example.com");
                p.setApproved(true);
                return patientRepo.save(p);
            });

            Patient p = patientRepo.findByUsername("patient1").orElseThrow();
            if (apptRepo.count() == 0) {
                Doctor d1 = doctorRepo.findAll().get(0);

                Appointment a1 = new Appointment();
                a1.setPatient(p);
                a1.setDoctor(d1);
                a1.setAppointmentDateTime(LocalDateTime.now().plusDays(2));
                a1.setReason("Initial consultation");
                a1.setStatus("SCHEDULED");
                apptRepo.save(a1);

                Fee f1 = new Fee();
                f1.setAppointment(a1);
                f1.setAmount(new BigDecimal("500"));
                f1.setPaid(false);
                feeRepo.save(f1);

                Appointment a2 = new Appointment();
                a2.setPatient(p);
                a2.setDoctor(d1);
                a2.setAppointmentDateTime(LocalDateTime.now().minusDays(10));
                a2.setReason("Follow-up visit");
                a2.setStatus("COMPLETED");
                apptRepo.save(a2);

                Fee f2 = new Fee();
                f2.setAppointment(a2);
                f2.setAmount(new BigDecimal("800"));
                f2.setPaid(true);
                feeRepo.save(f2);
            }
        };
    }
}
