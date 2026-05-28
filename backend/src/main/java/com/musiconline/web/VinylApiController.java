package com.musiconline.web;

import com.musiconline.service.VinylService;
import com.musiconline.web.dto.VinylRequest;
import com.musiconline.web.dto.VinylResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinyls")
public class VinylApiController {

    private final VinylService vinylService;

    public VinylApiController(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    /** My vinyls for sale */
    @GetMapping("/mine")
    public List<VinylResponse> myVinyls(@AuthenticationPrincipal UserDetails ud) {
        return vinylService.myVinyls(ud.getUsername());
    }

    /** Add a vinyl listing */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VinylResponse create(@Valid @RequestBody VinylRequest req,
                                @AuthenticationPrincipal UserDetails ud,
                                HttpServletRequest http) {
        return vinylService.create(req, ud.getUsername(), http.getRemoteAddr());
    }

    /** Update a vinyl listing */
    @PutMapping("/{id}")
    public VinylResponse update(@PathVariable Long id,
                                @Valid @RequestBody VinylRequest req,
                                @AuthenticationPrincipal UserDetails ud,
                                HttpServletRequest http) {
        return vinylService.update(id, req, ud.getUsername(), http.getRemoteAddr());
    }

    /** Delete a vinyl listing */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails ud,
                       HttpServletRequest http) {
        vinylService.delete(id, ud.getUsername(), http.getRemoteAddr());
    }
}
