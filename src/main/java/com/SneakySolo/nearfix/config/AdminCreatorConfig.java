package com.SneakySolo.nearfix.config;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminCreatorConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("anshu@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setRole(Role.ADMIN);
                admin.setEmail("anshu@gmail.com");
                admin.setFullname("Anshu");
                admin.setPassword(passwordEncoder.encode("manishasah"));
                admin.setPhone("7117117117");

                userRepository.save(admin);
            }
        };
    }
}
