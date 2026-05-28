package com.musiconline.service;

import com.musiconline.domain.AppUser;
import com.musiconline.domain.Vinyl;
import com.musiconline.domain.VinylType;
import com.musiconline.repository.AppUserRepository;
import com.musiconline.repository.VinylRepository;
import com.musiconline.web.dto.VinylRequest;
import com.musiconline.web.dto.VinylResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VinylService {

    private final VinylRepository vinylRepository;
    private final AppUserRepository userRepository;
    private final AuditService auditService;

    public VinylService(VinylRepository vinylRepository,
                        AppUserRepository userRepository,
                        AuditService auditService) {
        this.vinylRepository = vinylRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public Page<VinylResponse> search(String query, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Vinyl> result;
        if (query == null || query.isBlank()) {
            result = vinylRepository.findByAvailableTrue(pageable);
        } else if (type != null && !type.isBlank() && !type.equalsIgnoreCase("all")) {
            try {
                VinylType vt = VinylType.valueOf(type.toUpperCase());
                result = vinylRepository.searchByType(query.trim(), vt, pageable);
            } catch (IllegalArgumentException e) {
                result = vinylRepository.searchAll(query.trim(), pageable);
            }
        } else {
            result = vinylRepository.searchAll(query.trim(), pageable);
        }
        return result.map(VinylService::toResponse);
    }

    public VinylResponse getById(Long id) {
        Vinyl v = vinylRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vinyl not found"));
        return toResponse(v);
    }

    public List<VinylResponse> myVinyls(String email) {
        AppUser user = findUser(email);
        return vinylRepository.findBySellerOrderByCreatedAtDesc(user)
                .stream().map(VinylService::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public VinylResponse create(VinylRequest req, String email, String ip) {
        AppUser user = findUser(email);
        Vinyl v = new Vinyl();
        mapRequest(req, v);
        v.setSeller(user);
        v = vinylRepository.save(v);
        auditService.log(email, user.getRole().name(), "CREATE_VINYL", "Vinyl",
                String.valueOf(v.getId()), v.getTitle() + " by " + v.getArtist(), ip);
        return toResponse(v);
    }

    @Transactional
    public VinylResponse update(Long id, VinylRequest req, String email, String ip) {
        Vinyl v = vinylRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vinyl not found"));
        AppUser user = findUser(email);
        // Only owner or admin can update
        if (!v.getSeller().getEmail().equalsIgnoreCase(email) &&
                user.getRole() != com.musiconline.domain.Role.ADMIN) {
            throw new AccessDeniedException("Not authorised to edit this vinyl");
        }
        mapRequest(req, v);
        v.setUpdatedAt(Instant.now());
        v = vinylRepository.save(v);
        auditService.log(email, user.getRole().name(), "UPDATE_VINYL", "Vinyl",
                String.valueOf(v.getId()), v.getTitle(), ip);
        return toResponse(v);
    }

    @Transactional
    public void delete(Long id, String email, String ip) {
        Vinyl v = vinylRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vinyl not found"));
        AppUser user = findUser(email);
        if (!v.getSeller().getEmail().equalsIgnoreCase(email) &&
                user.getRole() != com.musiconline.domain.Role.ADMIN) {
            throw new AccessDeniedException("Not authorised to delete this vinyl");
        }
        auditService.log(email, user.getRole().name(), "DELETE_VINYL", "Vinyl",
                String.valueOf(v.getId()), v.getTitle(), ip);
        vinylRepository.delete(v);
    }

    // Admin: all vinyls paged
    public Page<VinylResponse> adminAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return vinylRepository.findAllByOrderByCreatedAtDesc(pageable).map(VinylService::toResponse);
    }

    private AppUser findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    private static void mapRequest(VinylRequest req, Vinyl v) {
        v.setTitle(req.title().trim());
        v.setArtist(req.artist().trim());
        v.setType(VinylType.valueOf(req.type().toUpperCase()));
        v.setGenre(req.genre());
        v.setLabel(req.label());
        v.setReleaseDate(req.releaseDate());
        v.setPrice(req.price());
        v.setCondition(req.condition());
        v.setDescription(req.description());
        v.setImageUrl(req.imageUrl());
        v.setAvailable(req.available() == null || req.available());
    }

    public static VinylResponse toResponse(Vinyl v) {
        return new VinylResponse(
                v.getId(),
                v.getTitle(),
                v.getArtist(),
                v.getType().name(),
                v.getGenre(),
                v.getLabel(),
                v.getReleaseDate(),
                v.getPrice(),
                v.getCondition(),
                v.getDescription(),
                v.getImageUrl(),
                v.isAvailable(),
                v.getSeller().getEmail(),
                v.getSeller().getUsername(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}
