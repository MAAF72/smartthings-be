package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.UUID;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class UserRepositoryImpl extends BaseRepository<User, UUID> implements UserRepository {
  
  @Inject
  public UserRepositoryImpl(SessionFactory sessionFactory) {
    super(User.class, sessionFactory);
  }

  @WithSpan
  public Uni<User> findByEmail(String email) {
    return findOne((cb, root) -> cb.equal(root.get("email"), email));
  }
  
  @WithSpan
  public Uni<List<UserWithTotalRegisteredDevices>> findAllUserWithTotalRegisteredDevices(PaginationRequest page) {
    return sessionFactory.withSession(session -> {
      String hql = """
        SELECT NEW %s(
          u,
          COUNT(d.id)
        )
        FROM %s u
        LEFT JOIN %s d ON d.registeredBy = u AND 1 = 1
        WHERE u.role = :role
        GROUP BY u
        """
        .formatted(UserWithTotalRegisteredDevices.class.getName(), User.class.getSimpleName(), Device.class.getSimpleName());
      return session.createQuery(hql, UserWithTotalRegisteredDevices.class)
        .setParameter("role", Role.ST_USERS)
        .setFirstResult(page.offset())
        .setMaxResults(page.size)
        .getResultList();
    });
  }
  
  @WithSpan
  public Uni<Long> countAllUser() {
    return countAll((cb, root) -> cb.equal(root.get("role"), Role.ST_USERS));
  }

  @WithSpan
  public Uni<List<UserWithTotalRegisteredDevices>> findAllVendorWithTotalRegisteredDevices(PaginationRequest page) {
    return sessionFactory.withSession(session -> {
      String hql = """
        SELECT NEW %s(
            u,
            COUNT(d.id)
        )
        FROM %s u
        LEFT JOIN %s d ON d.createdBy = u AND d.registeredBy IS NOT NULL AND 1 = 1
        WHERE u.role = :role
        GROUP BY u
        """
        .formatted(UserWithTotalRegisteredDevices.class.getName(), User.class.getSimpleName(), Device.class.getSimpleName());
      return session.createQuery(hql, UserWithTotalRegisteredDevices.class)
          .setParameter("role", Role.DEVICE_VENDOR)
          .setFirstResult(page.offset())
          .setMaxResults(page.size)
          .getResultList();
    });
  }
  
  @WithSpan
  public Uni<Long> countAllVendor() {
    return countAll((cb, root) -> cb.equal(root.get("role"), Role.DEVICE_VENDOR));
  }
}
