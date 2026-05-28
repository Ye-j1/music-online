package com.musiconline.service;

import com.musiconline.domain.AuditLogEntry;
import com.musiconline.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public void log(String actor, String actorRole, String action,
                    String entityType, String entityId, String detail, String ip) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setActor(actor);
        entry.setActorRole(actorRole);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetail(detail != null && detail.length() > 500 ? detail.substring(0, 500) : detail);
        entry.setIpAddress(ip);
        entry.setOccurredAt(Instant.now());
        repo.save(entry);
    }
}
