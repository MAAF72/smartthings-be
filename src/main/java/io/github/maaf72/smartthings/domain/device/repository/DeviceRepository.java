package io.github.maaf72.smartthings.domain.device.repository;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;
import io.smallrye.mutiny.Uni;

public interface DeviceRepository extends AppRepositoryItf<Device, UUID> {
  Uni<List<Device>> findAllAvailableDevice(PaginationRequest page);

  Uni<Long> countAllAvailableDevice();

  Uni<List<Device>> findAllVendorDevice(UUID vendorId, PaginationRequest page);
  
  Uni<Long> countAllVendorDevice(UUID vendorId);

  Uni<List<Device>> findAllUserDevice(UUID userId, PaginationRequest page);

  Uni<Long> countAllUserDevice(UUID userId);
}
