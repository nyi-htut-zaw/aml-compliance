package com.bank.aml.audit.service;

import com.bank.aml.audit.model.AuditEvent;
import com.bank.aml.audit.repository.AuditEventRepository;
import com.bank.aml.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public AuditEvent recordEvent(String module, String action, String entityType, String entityId, String details) {
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserPrincipal()
                .map(p -> String.join(",", p.getRoles()))
                .orElse("ANONYMOUS");

        AuditEvent event = new AuditEvent(
                UUID.randomUUID().toString(),
                username,
                role,
                "127.0.0.1",
                module,
                action,
                entityType,
                entityId,
                details
        );
        log.info("[AUDIT] [{}] [{}] Actor: {} Target: {}:{} - {}", module, action, username, entityType, entityId, details);
        return auditEventRepository.save(event);
    }

    @Transactional
    public AuditEvent recordEventWithState(String module, String action, String entityType, String entityId,
                                           String beforeState, String afterState, String details) {
        AuditEvent event = recordEvent(module, action, entityType, entityId, details);
        event.setBeforeStateJson(beforeState);
        event.setAfterStateJson(afterState);
        return auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Page<AuditEvent> getAuditEvents(String module, Pageable pageable) {
        if (module != null && !module.isBlank()) {
            return auditEventRepository.findByModule(module.toUpperCase(), pageable);
        }
        return auditEventRepository.findAll(pageable);
    }
}
