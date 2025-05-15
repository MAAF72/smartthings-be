package io.github.maaf72.smartthings.domain.auditlog.repository;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.auditlog.entity.AuditLog;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuditLogRepositoryImpl extends BaseRepository<AuditLog, UUID> implements AuditLogRepository {

  public AuditLogRepositoryImpl() {
    super(AuditLog.class);
  }
}
