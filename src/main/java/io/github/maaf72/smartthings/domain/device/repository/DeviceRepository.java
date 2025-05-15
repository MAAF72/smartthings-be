package io.github.maaf72.smartthings.domain.device.repository;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;

public interface DeviceRepository extends AppRepositoryItf<Device, UUID> {
  List<Device> findAllAvailableDevice(PaginationRequest page);

  long countAllAvailableDevice();

  List<Device> findAllVendorDevice(UUID vendorId, PaginationRequest page);
  
  long countAllVendorDevice(UUID vendorId);

  List<Device> findAllUserDevice(UUID userId, PaginationRequest page);

  long countAllUserDevice(UUID userId);
}
