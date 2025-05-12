package io.github.maaf72.smartthings.domain.device.repository;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.infra.database.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeviceRepositoryImpl extends BaseRepository<Device, UUID> implements DeviceRepository {

  public DeviceRepositoryImpl() {
    super(Device.class);
  }

  public List<Device> findAllAvailableDevice(PaginationRequest page) {
    return findAll((cb, root) -> cb.isNotNull(root.get("registeredBy")), page);
  }

  public long countAllAvailableDevice() {
   return countAll((cb, root) -> cb.isNotNull(root.get("registeredBy")));
  }

  public List<Device> findAllVendorDevice(UUID vendorId, PaginationRequest page) {
    return findAll((cb, root) -> cb.equal(root.get("createdBy"), vendorId), page);
  }

  public long countAllVendorDevice(UUID vendorId) {
    return countAll((cb, root) -> cb.equal(root.get("createdBy"), vendorId));
  }

  public List<Device> findAllUserDevice(UUID userId, PaginationRequest page) {
    return findAll((cb, root) -> cb.equal(root.get("registeredBy"), userId), page);
  }

  public long countAllUserDevice(UUID userId) {
    return countAll((cb, root) -> cb.equal(root.get("registeredBy"), userId));
  }
}
