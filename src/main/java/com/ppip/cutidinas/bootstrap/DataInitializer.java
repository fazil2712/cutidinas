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

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User(
                    "000000",
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
