package com.musiconline.web;

import com.musiconline.domain.AuditLogEntry;
import com.musiconline.repository.AdminRepository;
import com.musiconline.repository.AuditLogRepository;
import com.musiconline.repository.AppUserRepository;
import com.musiconline.service.VinylService;
import com.musiconline.web.dto.VinylResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private final VinylService vinylService;
    private final AppUserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminApiController(VinylService vinylService,
                              AppUserRepository userRepository,
                              AdminRepository adminRepository,
                              AuditLogRepository auditLogRepository) {
        this.vinylService = vinylService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /** Admin: all vinyls paged */
    @GetMapping("/vinyls")
    public Page<VinylResponse> allVinyls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return vinylService.adminAll(page, Math.min(size, 100));
    }

    /** Admin: all users summary (mo_users) */
    @GetMapping("/users")
    public Object allUsers() {
        return userRepository.findAll().stream().map(u -> Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "username", u.getUsername(),
                "role", u.getRole().name(),
                "enabled", u.isEnabled(),
                "createdAt", u.getCreatedAt()
        )).toList();
    }

    /** Admin: admins table (mo_admins) — PDF Task 2 dedicated admin table */
    @GetMapping("/admins")
    public Object allAdmins() {
        return adminRepository.findAll().stream().map(a -> Map.of(
                "id", a.getId(),
                "email", a.getEmail(),
                "username", a.getUsername(),
                "adminLevel", a.getAdminLevel(),
                "active", a.isActive(),
                "createdAt", a.getCreatedAt(),
                "lastLoginAt", a.getLastLoginAt()
        )).toList();
    }

    /** Admin: audit log */
    @GetMapping("/audit")
    public Page<AuditLogEntry> auditLog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return auditLogRepository.findAllByOrderByOccurredAtDesc(PageRequest.of(page, Math.min(size, 200)));
    }

    /** Admin: delete vinyl */
    @DeleteMapping("/vinyls/{id}")
    public void deleteVinyl(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud,
                            HttpServletRequest http) {
        vinylService.delete(id, ud.getUsername(), http.getRemoteAddr());
    }
}
