package com.springblog.configuration;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.springblog.Entities.User;
import com.springblog.Repository.UserRepo;

import jakarta.annotation.PostConstruct;

@Component
public class AdminInitializer {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminIfNotExists() {

        if (userRepo.existsByRole("ROLE_ADMIN")) {
            return; // admin already exists
        }

        User admin = new User();
        admin.setUsername(adminEmail);
        admin.setUsername(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ROLE_ADMIN");
        admin.setEnabled(true);
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());

        userRepo.save(admin);

        System.out.println("✅ Admin user created");
    }
}
