package io.github.maaf72.smartthings.domain.auditlog.repository;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.auditlog.entity.AuditLog;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;

public interface AuditLogRepository extends AppRepositoryItf<AuditLog, UUID> {}
