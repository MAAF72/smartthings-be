package io.github.maaf72.smartthings.domain.device.repository;

import java.util.List;
import java.util.UUID;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeviceRepositoryImpl extends BaseRepository<Device, UUID> implements DeviceRepository {

  @Inject
  public DeviceRepositoryImpl(SessionFactory sessionFactory) {
    super(Device.class, sessionFactory);
  }

  public Uni<List<Device>> findAllAvailableDevice(PaginationRequest page) {
    return findAll((cb, root) -> cb.isNull(root.get("registeredBy")), page);
  }

  public Uni<Long> countAllAvailableDevice() {
   return countAll((cb, root) -> cb.isNull(root.get("registeredBy")));
  }

  public Uni<List<Device>> findAllVendorDevice(UUID vendorId, PaginationRequest page) {
    return findAll((cb, root) -> cb.equal(root.get("createdBy").get("id"), vendorId), page);
  }

  public Uni<Long> countAllVendorDevice(UUID vendorId) {
    return countAll((cb, root) -> cb.equal(root.get("createdBy").get("id"), vendorId));
  }

  public Uni<List<Device>> findAllUserDevice(UUID userId, PaginationRequest page) {
    return findAll((cb, root) -> cb.equal(root.get("registeredBy").get("id"), userId), page);
  }

  public Uni<Long>  countAllUserDevice(UUID userId) {
    return countAll((cb, root) -> cb.equal(root.get("registeredBy").get("id"), userId));
  }
}
