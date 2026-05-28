package com.musiconline.repository;

import com.musiconline.domain.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
    Page<AuditLogEntry> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
