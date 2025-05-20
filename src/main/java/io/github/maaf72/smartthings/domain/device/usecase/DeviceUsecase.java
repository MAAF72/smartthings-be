package io.github.maaf72.smartthings.domain.device.usecase;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.dto.CreateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.UpdateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.itf.AppUsecaseItf;
import io.smallrye.mutiny.Uni;

public interface DeviceUsecase extends AppUsecaseItf {
  Uni<Device> createDevice(UUID actorId, Role role, CreateVendorDeviceRequest request);

  Uni<List<Device>> listDevice(UUID actorId, Role role, PaginationRequest page);

  Uni<Long> countDevice(UUID actorId, Role role);
  
  Uni<List<Device>> listAvailableDevice(UUID actorId, PaginationRequest page);
  
  Uni<Long> countAvailableDevice();

  Uni<Device> getDevice(UUID actorId, Role role, UUID deviceId);

  Uni<Device> updateDevice(UUID actorId, Role role, UpdateVendorDeviceRequest request, UUID deviceId);

  Uni<Void> deleteDevice(UUID actorId, Role role, UUID deviceId);

  Uni<Void> registerDevice(UUID actorId, Role role, UUID deviceId);
  
  Uni<Void> unregisterDevice(UUID actorId, Role role, UUID deviceId);
  
}
