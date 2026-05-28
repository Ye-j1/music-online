package com.musiconline.web;

import com.musiconline.service.VinylService;
import com.musiconline.web.dto.VinylResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final VinylService vinylService;

    public PublicController(VinylService vinylService) {
        this.vinylService = vinylService;
    }

    /** Public vinyl search — artist, album/ep/single title */
    @GetMapping("/vinyls")
    public Page<VinylResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return vinylService.search(q, type, page, Math.min(size, 50));
    }

    /** Public vinyl detail */
    @GetMapping("/vinyls/{id}")
    public VinylResponse getById(@PathVariable Long id) {
        return vinylService.getById(id);
    }
}
