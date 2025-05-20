package io.github.maaf72.smartthings.domain.auditlog.repository;

import java.util.UUID;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.github.maaf72.smartthings.domain.auditlog.entity.AuditLog;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditLogRepositoryImpl extends BaseRepository<AuditLog, UUID> implements AuditLogRepository {

  @Inject
  public AuditLogRepositoryImpl(SessionFactory sessionFactory) {
    super(AuditLog.class, sessionFactory);
  }
}
