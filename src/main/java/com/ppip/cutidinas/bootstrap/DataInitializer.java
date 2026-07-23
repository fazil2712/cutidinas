package com.ppip.cutidinas.bootstrap;

import com.ppip.cutidinas.model.User;
import com.ppip.cutidinas.repository.UserRepository;
import com.ppip.cutidinas.service.CutiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CutiService cutiService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE pengajuan_cuti ADD COLUMN IF NOT EXISTS file_pendukung VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE pengajuan_cuti ADD COLUMN IF NOT EXISTS approver_badgeid VARCHAR(255)");
            jdbcTemplate.execute("ALTER TABLE pengajuan_cuti ADD COLUMN IF NOT EXISTS alasan_penolakan VARCHAR(255)");
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_permissions (user_badgeid VARCHAR(255) NOT NULL, permission VARCHAR(255) DEFAULT NULL)");
        } catch (Exception e) {
            System.err.println("DB Migration warning: " + e.getMessage());
        }
        if (userRepository.count() == 0) {
            User admin = new User(
                    "000000",
                    "3200000000000001",
                    "admin",
                    passwordEncoder.encode("admin"),
                    "IT",
                    "admin@ppip.com",
                    null,
                    LocalDate.of(2020, 1, 1),
                    Arrays.asList("ROLE_ADMIN", "ROLE_USER")
            );
            userRepository.save(admin);
            cutiService.generateCutiIfNeeded(admin);
            
            User user = new User(
                    "111111",
                    "3200000000000002",
                    "user1",
                    passwordEncoder.encode("user"),
                    "HR",
                    "employee@ppip.com",
                    null,
                    LocalDate.of(2022, 1, 1),
                    Arrays.asList("ROLE_USER")
            );
            userRepository.save(user);
            cutiService.generateCutiIfNeeded(user);
            System.out.println("==========================================================");
            System.out.println("Default users created!");
            System.out.println("Login as Admin:    Username = 000000 or 'admin',    Password = admin");
            System.out.println("Login as Employee: Username = 111111 or 'user1', Password = user");
            System.out.println("==========================================================");
        }
    }
}
