package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class UserRepositoryImpl extends BaseRepository<User, UUID> implements UserRepository {
  
  public UserRepositoryImpl() {
    super(User.class);
  }

  public Optional<User> findByEmail(String email) {
    return findOne((cb, root) -> cb.equal(root.get("email"), email));
  }
  
  public List<UserWithTotalRegisteredDevices> findAllUserWithTotalRegisteredDevices(PaginationRequest page) {
    return doInSession(session -> {
      String hql = """
        SELECT NEW %s(
          u,
          COUNT(d.id)
        )
        FROM %s u
        LEFT JOIN %s d ON d.registeredBy = u
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
  
  public long countAllUser() {
    return countAll((cb, root) -> cb.equal(root.get("role"), Role.ST_USERS));
  }

  public List<UserWithTotalRegisteredDevices> findAllVendorWithTotalRegisteredDevices(PaginationRequest page) {
    return doInSession(session -> {
      String hql = """
        SELECT NEW %s(
            u,
            COUNT(d.id)
        )
        FROM %s u
        LEFT JOIN %s d ON d.createdBy = u AND d.registeredBy IS NOT NULL
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
  
  public long countAllVendor() {
    return countAll((cb, root) -> cb.equal(root.get("role"), Role.DEVICE_VENDOR));
  }
}
