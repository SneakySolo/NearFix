package com.SneakySolo.nearfix.service;

import com.SneakySolo.nearfix.domain.user.Role;
import com.SneakySolo.nearfix.domain.user.User;
import com.SneakySolo.nearfix.dto.RegisterDTO;
import com.SneakySolo.nearfix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerCustomer(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setFullname(dto.getFullname());
        user.setRole(dto.getRole());

        userRepository.save(user);
        return "success";
    }

    public User login (String email, String password){
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        if (!user.isEnabled()) {
            return null;
        }

        return user;
    }

    public List<User> getAllByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public void toggleUserStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        if (user.isEnabled()) {
            user.setEnabled(false);
        }
        else {
            user.setEnabled(true);
        }

        userRepository.save(user);
    }
}
