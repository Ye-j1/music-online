package com.musiconline.repository;

import com.musiconline.domain.AppUser;
import com.musiconline.domain.Vinyl;
import com.musiconline.domain.VinylType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VinylRepository extends JpaRepository<Vinyl, Long> {

    // Search by artist, title across all types
    @Query("SELECT v FROM Vinyl v WHERE v.available = true AND (" +
           "LOWER(v.artist) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Vinyl> searchAll(@Param("q") String query, Pageable pageable);

    // Search filtered by type
    @Query("SELECT v FROM Vinyl v WHERE v.available = true AND v.type = :type AND (" +
           "LOWER(v.artist) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(v.title) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Vinyl> searchByType(@Param("q") String query, @Param("type") VinylType type, Pageable pageable);

    // All available vinyls for public browsing
    Page<Vinyl> findByAvailableTrue(Pageable pageable);

    // Vinyls by seller
    List<Vinyl> findBySellerOrderByCreatedAtDesc(AppUser seller);

    // Admin: all vinyls
    Page<Vinyl> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
