package com.musiconline.web;

import com.musiconline.service.AuthService;
import com.musiconline.web.dto.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.musiconline.repository.AppUserRepository;

@RestController
@RequestMapping("/api/me")
public class ProfileApiController {

    private final AppUserRepository userRepository;

    public ProfileApiController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal UserDetails ud) {
        var user = userRepository.findByEmailIgnoreCase(ud.getUsername())
                .orElseThrow();
        return AuthService.toUserResponse(user);
    }
}
