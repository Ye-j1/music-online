package com.musiconline.service;

import com.musiconline.domain.AppUser;
import com.musiconline.domain.Role;
import com.musiconline.repository.AppUserRepository;
import com.musiconline.security.JwtService;
import com.musiconline.web.dto.LoginResponse;
import com.musiconline.web.dto.RegisterRequest;
import com.musiconline.web.dto.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    public AuthService(AppUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
    }

    public LoginResponse login(String email, String password) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, password));
        AppUser user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", toUserResponse(user));
    }

    @Transactional
    public void register(RegisterRequest req, String ip) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (req.password() == null || req.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        Role role = "retailer".equalsIgnoreCase(req.role()) ? Role.RETAILER : Role.USER;

        AppUser user = new AppUser();
        user.setEmail(req.email().trim().toLowerCase(Locale.ROOT));
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setUsername(req.username().trim());
        user.setRole(role);
        user = userRepository.save(user);

        auditService.log(user.getEmail(), role.name(), "REGISTER", "User",
                String.valueOf(user.getId()), "New account: " + role, ip);
    }

    public static UserResponse toUserResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getRole().name());
    }
}
